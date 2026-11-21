package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Audible;

import java.util.List;

/** Las granadas guardadas y una C4, todas tratadas como Audible. */
public interface SonidoService {

    List<Audible> sonidos(String sitio, int segundos);
}
