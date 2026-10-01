package br.com.esports.domain;

public enum StatusTorneio {
    INSCRICOES_ABERTAS("Inscricoes abertas"),
    EM_ANDAMENTO("Em andamento"),
    FINALIZADO("Finalizado");

    private final String descricao;

    StatusTorneio(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
