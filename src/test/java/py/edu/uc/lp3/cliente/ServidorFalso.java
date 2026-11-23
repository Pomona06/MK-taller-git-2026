package py.edu.uc.lp3.cliente;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Un servidor HTTP de verdad, en un puerto libre, que responde siempre lo que
 * se le indique. Hace de compañero (A2 o A3) en los tests, sin depender de que
 * su servicio esté prendido. Guarda el último cuerpo que recibió.
 */
public class ServidorFalso implements AutoCloseable {

    private final HttpServer servidor;
    private volatile int status = 200;
    private volatile String respuesta = "{}";
    private volatile String ultimoPedido;
    private volatile String ultimaRuta;
    private volatile int pedidos;
    private volatile String ultimoLargo;

    public ServidorFalso() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        servidor.createContext("/", intercambio -> {
            ultimaRuta = intercambio.getRequestURI().getPath();
            ultimoLargo = intercambio.getRequestHeaders().getFirst("Content-Length");
            ultimoPedido = new String(intercambio.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            pedidos++;
            byte[] cuerpo = respuesta.getBytes(StandardCharsets.UTF_8);
            intercambio.getResponseHeaders().add("Content-Type", "application/json");
            intercambio.sendResponseHeaders(status, cuerpo.length);
            try (OutputStream salida = intercambio.getResponseBody()) {
                salida.write(cuerpo);
            }
        });
        servidor.start();
    }

    public void responder(int status, String respuesta) {
        this.status = status;
        this.respuesta = respuesta;
        this.pedidos = 0;
    }

    public String url() {
        return "http://localhost:" + servidor.getAddress().getPort();
    }

    public String getUltimoPedido() {
        return ultimoPedido;
    }

    public String getUltimaRuta() {
        return ultimaRuta;
    }

    /** El encabezado Content-Length del último pedido (null si llegó por partes). */
    public String getUltimoLargo() {
        return ultimoLargo;
    }

    public int getPedidos() {
        return pedidos;
    }

    @Override
    public void close() {
        servidor.stop(0);
    }
}
