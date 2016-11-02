package ru.smartexpress.courierapp.request;

import ru.smartexpress.common.dto.PasswordResetCodeDTO;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 01.11.16 17:15
 */
public class PasswordResetCode extends AbstractSpringAndroidRequest {

    private PasswordResetCodeDTO passwordResetCodeDTO;


    public PasswordResetCode(PasswordResetCodeDTO passwordResetCodeDTO) {
        super(Object.class);
        this.passwordResetCodeDTO = passwordResetCodeDTO;
    }

    @Override
    public Object loadDataFromNetwork() throws Exception {
        String url = baseUrl+"/courier/resetPassword";
        getRestTemplate().put(url, passwordResetCodeDTO);
        return null;
    }
}
