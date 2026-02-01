/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mvc_bateau.infra.exception;

/**
 *
 * @author macbook
 */
public class DBException extends RuntimeException {
    
    public DBException() {
        // TODO Auto-generated constructor stub
    }

    public DBException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public DBException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public DBException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public DBException(String message, Throwable cause, boolean enableSuppression,
                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }
    
}
