package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.constants.ReglasJuego;
import py.edu.uc.lp3.domain.Audible;
import py.edu.uc.lp3.domain.BombaC4;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;
import py.edu.uc.lp3.repository.GranadaRepository;
import py.edu.uc.lp3.service.SonidoService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SonidoServiceImpl implements SonidoService {

    private final GranadaRepository granadaRepository;

    public SonidoServiceImpl(GranadaRepository granadaRepository) {
        this.granadaRepository = granadaRepository;
    }

    /**
     * La C4 solo se planta en el sitio A o B y su temporizador va de 40 a 0
     * segundos. Una granada ya lanzada desapareció: no entra en la lista.
     */
    @Override
    public List<Audible> sonidos(String sitio, int segundos) {
        String sitioC4 = sitio == null ? "" : sitio.strip().toUpperCase();
        if (!"A".equals(sitioC4) && !"B".equals(sitioC4)) {
            throw new ValorInvalidoException("el sitio debe ser A o B (recibido: " + sitio + ")");
        }
        if (segundos < 0 || segundos > ReglasJuego.SEGUNDOS_C4) {
            throw new ValorInvalidoException("los segundos restantes deben estar entre 0 y " + ReglasJuego.SEGUNDOS_C4);
        }
        List<Audible> audibles = new ArrayList<>(granadaRepository.findByLanzadaFalse());
        audibles.add(new BombaC4(sitioC4, segundos));
        return audibles;
    }
}
