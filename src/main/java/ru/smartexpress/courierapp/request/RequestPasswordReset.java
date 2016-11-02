package ru.smartexpress.courierapp.request;

import ru.smartexpress.common.dto.PasswordResetRequestDTO;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 01.11.16 16:36
 */
public class RequestPasswordReset extends AbstractSpringAndroidRequest<PasswordResetRequestDTO> {
    private String phone;

    public RequestPasswordReset(String phone) {
        super(PasswordResetRequestDTO.class);
        this.phone = phone;
    }

    @Override
    public PasswordResetRequestDTO loadDataFromNetwork() throws Exception {
        String url = baseUrl + "/courier/requestPasswordReset?phone="+phone;
        return getRestTemplate().getForObject(url, PasswordResetRequestDTO.class);
    }
}
