package ru.smartexpress.courierapp.service;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 23.12.14 7:47
 */
public interface RegistrationService {
    void register(String phone, String name, String password);
}
