package br.com.emanuelgabriel.model;

public interface Notificador {

  void enviarMensagem(String titulo, String link, String dataPublicacao);

}
