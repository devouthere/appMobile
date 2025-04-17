// LoginActivityTest.java
package com.example.app;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LoginActivityTest {

    public enum ResultadoRedirecionamento {
        BARBEIRO,
        CLIENTE,
        ERRO
    }

    public ResultadoRedirecionamento redirecionarPorTipoUsuario(String tipoUsuario) {
        if ("barbeiro".equalsIgnoreCase(tipoUsuario)) {
            return ResultadoRedirecionamento.BARBEIRO;
        } else if ("cliente".equalsIgnoreCase(tipoUsuario)) {
            return ResultadoRedirecionamento.CLIENTE;
        } else {
            return ResultadoRedirecionamento.ERRO;
        }
    }

    @Test
    public void testRedirecionamentoBarbeiro() {
        ResultadoRedirecionamento resultado = redirecionarPorTipoUsuario("barbeiro");
        assertEquals(ResultadoRedirecionamento.BARBEIRO, resultado);
    }

    @Test
    public void testRedirecionamentoCliente() {
        ResultadoRedirecionamento resultado = redirecionarPorTipoUsuario("cliente");
        assertEquals(ResultadoRedirecionamento.CLIENTE, resultado);
    }

    @Test
    public void testRedirecionamentoInvalido() {
        ResultadoRedirecionamento resultado = redirecionarPorTipoUsuario("admin");
        assertEquals(ResultadoRedirecionamento.ERRO, resultado);
    }

    @Test
    public void testRedirecionamentoComCaixaAlta() {
        ResultadoRedirecionamento resultado = redirecionarPorTipoUsuario("CLIENTE");
        assertEquals(ResultadoRedirecionamento.CLIENTE, resultado);
    }

    @Test
    public void testRedirecionamentoNulo() {
        ResultadoRedirecionamento resultado = redirecionarPorTipoUsuario(null);
        assertEquals(ResultadoRedirecionamento.ERRO, resultado);
    }
}
