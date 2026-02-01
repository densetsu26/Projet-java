/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_bateau.infra.misc;

import java.util.Objects;

/**
 *
 * @author macbook
 */
public class StringUtils {
    
    private StringUtils(){}
    
    public static boolean isBlank(String s) {
        return Objects.isNull(s) || s.isBlank();
    }
}
