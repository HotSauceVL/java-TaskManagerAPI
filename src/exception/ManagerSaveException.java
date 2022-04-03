package exception;

public class ManagerSaveException extends RuntimeException { // Есть вероятность, что я не до конца понял ТЗ,
    // поэтому просьба, при наличии явных ошибок с ТЗ, объяснить точнее что от меня требуется=)
    // поведение ManagerSaveException в ТЗ вообще не описано =(

    public ManagerSaveException(final String message) {
        super(message);
    }
}
