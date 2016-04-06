package ru.smartexpress.courierapp.core;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 30.03.16 19:16
 */
public interface ExceptionHandler {
// ------------------------------ FIELDS ------------------------------

    ExceptionHandler DEFAULT = new ExceptionHandler() {
        @Override
        public boolean onError(Throwable t){
            throw new RuntimeException(t);
        }
    };

    ExceptionHandler LOGGING_HANDLER = new ExceptionHandler() {
        @Override
        public boolean onError(Throwable t){
            Logger.error(t, "Error during execution of task on dispatcher: continue with next request");
            return true;
        }
    };

// -------------------------- OTHER METHODS --------------------------

    boolean onError(Throwable t);
}
