package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import py.edu.uc.lp3.constants.Categoria;
import py.edu.uc.lp3.constants.CatalogoArmas;
import py.edu.uc.lp3.constants.ReglasJuego;
import py.edu.uc.lp3.exceptions.GranadaUsadaException;
import py.edu.uc.lp3.exceptions.JugadorEliminadoException;
import py.edu.uc.lp3.exceptions.MunicionInsuficienteException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;
import py.edu.uc.lp3.service.ReglasService;
import py.edu.uc.lp3.service.dto.ConsultaDisparoRequest;
import py.edu.uc.lp3.service.dto.ConsultaDisparoResponse;
import py.edu.uc.lp3.service.dto.ConsultaImpactoRequest;
import py.edu.uc.lp3.service.dto.ConsultaImpactoResponse;
import py.edu.uc.lp3.service.dto.ConsultaRecargaRequest;
import py.edu.uc.lp3.service.dto.ConsultaRecargaResponse;
import py.edu.uc.lp3.service.dto.ReglaResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Las mismas dos reglas que usan la simulación y los disparos, pero sobre un
 * estado que llega en el JSON. No usa repositorios: nunca lee ni cambia la
 * base. Responde 200 si la regla lo permite; si no, lanza la misma excepción
 * que el resto del servicio (400 dato imposible, 409 regla incumplida).
 */
@Service
public class ReglasServiceImpl implements ReglasService {

    @Override
    public List<ReglaResponse> reglas() {
        return List.of(
                new ReglaResponse("Regla de munición",
                        "Un arma de fuego solo dispara si el cargador tiene las balas de ese disparo (una, o tres "
                                + "en ráfaga) y solo recarga si le queda un cargador de reserva, y una granada se "
                                + "lanza una sola vez.",
                        "POST /api/reglas/disparo y POST /api/reglas/recarga",
                        "409 MunicionInsuficienteException o GranadaUsadaException"),
                new ReglaResponse("Regla de vida",
                        "El daño baja con la distancia (la bala llega hasta el alcance del arma), el chaleco "
                                + "desvía una parte según la relación de blindaje del arma, la vida queda entre 0 y "
                                + "100 y un jugador eliminado no recibe más daño.",
                        "POST /api/reglas/impacto",
                        "409 JugadorEliminadoException"));
    }

    @Override
    public ConsultaDisparoResponse consultarDisparo(ConsultaDisparoRequest p) {
        CatalogoArmas modelo = modeloDel(p == null ? null : p.modelo());
        int veces = p.veces() == null ? 1 : p.veces();
        if (veces < 1) {
            throw new ValorInvalidoException("la cantidad de disparos debe ser al menos 1");
        }
        if (modelo.getCategoria() == Categoria.GRANADA) {
            if (Boolean.TRUE.equals(p.lanzada())) {
                throw new GranadaUsadaException(modelo.getNombre());
            }
            if (veces > 1) {
                throw new ValorInvalidoException("una granada se lanza una sola vez: veces debe ser 1");
            }
            return new ConsultaDisparoResponse(true, modelo.getNombre(), 0, null,
                    modelo.getNombre() + " se puede lanzar; después queda usada");
        }
        int carga = cargaDel(modelo, p.carga());
        boolean rafaga = Boolean.TRUE.equals(p.modoRafaga());
        if (rafaga && !modelo.isTieneRafaga()) {
            throw new ValorInvalidoException(modelo.getNombre()
                    + " no tiene modo ráfaga: en CS2 solo lo tienen la Glock-18 y la FAMAS");
        }
        // long: con un int, 3 balas × 715 millones de ráfagas se desborda y da un número negativo.
        long necesarias = (long) (rafaga ? ReglasJuego.BALAS_POR_RAFAGA : 1) * veces;
        if (carga < necesarias) {
            throw new MunicionInsuficienteException(modelo.getNombre() + " no puede disparar: el cargador tiene "
                    + carga + " balas y hacen falta " + necesarias);
        }
        return new ConsultaDisparoResponse(true, modelo.getNombre(), (int) necesarias, carga - (int) necesarias,
                modelo.getNombre() + " puede disparar " + veces + (veces == 1 ? " vez" : " veces"));
    }

    @Override
    public ConsultaRecargaResponse consultarRecarga(ConsultaRecargaRequest p) {
        CatalogoArmas modelo = modeloDel(p == null ? null : p.modelo());
        if (modelo.getCategoria() == Categoria.GRANADA) {
            throw new ValorInvalidoException(modelo.getNombre() + " es una granada: no se recarga");
        }
        int carga = cargaDel(modelo, p.carga());
        int reserva = p.cargadoresRestantes() == null ? modelo.getCargadoresDeReserva() : p.cargadoresRestantes();
        if (reserva < 0 || reserva > modelo.getCargadoresDeReserva()) {
            throw new ValorInvalidoException("los cargadores de reserva de " + modelo.getNombre()
                    + " van de 0 a " + modelo.getCargadoresDeReserva());
        }
        if (carga == modelo.getCargador()) {
            return new ConsultaRecargaResponse(true, modelo.getNombre(), carga, reserva,
                    modelo.getNombre() + " ya tiene el cargador lleno: no hace falta recargar");
        }
        if (reserva == 0) {
            throw new MunicionInsuficienteException(modelo.getNombre() + " no tiene cargadores de reserva");
        }
        return new ConsultaRecargaResponse(true, modelo.getNombre(), modelo.getCargador(), reserva - 1,
                modelo.getNombre() + " puede recargar");
    }

    @Override
    public ConsultaImpactoResponse consultarImpacto(ConsultaImpactoRequest p) {
        CatalogoArmas modelo = modeloDel(p == null ? null : p.modelo());
        double distancia = ReglasDeArmas.distancia(p.distancia());
        ReglasDeArmas.verificarAlcance(modelo, distancia);
        int vida = p.vida() == null ? ReglasJuego.VIDA_INICIAL : p.vida();
        int blindaje = p.blindaje() == null ? 0 : p.blindaje();
        if (vida < 0 || vida > ReglasJuego.VIDA_INICIAL) {
            throw new ValorInvalidoException("la vida debe estar entre 0 y " + ReglasJuego.VIDA_INICIAL);
        }
        if (blindaje < 0 || blindaje > ReglasJuego.BLINDAJE_KEVLAR) {
            throw new ValorInvalidoException("el blindaje debe estar entre 0 y " + ReglasJuego.BLINDAJE_KEVLAR);
        }
        if (vida == 0) {
            throw new JugadorEliminadoException("El jugador");
        }
        int dano = ReglasDeArmas.construir(modelo, modelo.equipoUnico().orElse("CT")).danoA(distancia);
        int[] impacto = SimulacionServiceImpl.calcularImpacto(dano, modelo.getRelacionBlindaje(), blindaje, vida);
        int vidaDespues = vida - impacto[0];
        return new ConsultaImpactoResponse(true, modelo.getNombre(), dano, impacto[0], impacto[1], vidaDespues,
                blindaje - impacto[1], vidaDespues == 0);
    }

    private static CatalogoArmas modeloDel(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ValorInvalidoException("falta el modelo; el catálogo tiene: " + nombresDelCatalogo());
        }
        return CatalogoArmas.buscar(texto).orElseThrow(() -> new ValorInvalidoException("no existe el arma \""
                + texto + "\" en el catálogo de CS2; el catálogo tiene: " + nombresDelCatalogo()));
    }

    private static int cargaDel(CatalogoArmas modelo, Integer carga) {
        int valor = carga == null ? modelo.getCargador() : carga;
        if (valor < 0 || valor > modelo.getCargador()) {
            throw new ValorInvalidoException("la carga de " + modelo.getNombre() + " va de 0 a " + modelo.getCargador());
        }
        return valor;
    }

    private static String nombresDelCatalogo() {
        return Arrays.stream(CatalogoArmas.values()).map(CatalogoArmas::getNombre).collect(Collectors.joining(", "));
    }
}
