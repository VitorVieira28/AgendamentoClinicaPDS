package com.mycompany.agendamentoclinica;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ValidadorSeguranca {

    // Valida se o e-mail tem um formato básico aceitável
    public static boolean validarEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    // Valida se a senha tem pelo menos 6 caracteres (critério de senha forte básica)
    public static boolean validarSenhaForte(String senha) {
        return senha != null && senha.length() >= 6;
    }

    // Criptografa a senha usando SHA-256 antes de salvar
    public static String criptografarSenha(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criptografar senha", e);
        }
    }
}