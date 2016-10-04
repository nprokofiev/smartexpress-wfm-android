package ru.smartexpress.courierapp.request;

import ru.smartexpress.common.dto.AccountDTO;
import ru.smartexpress.common.dto.AccountRequest;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 03.10.16 19:20
 */
public class AccountRestRequest extends AbstractSpringAndroidRequest<AccountDTO> {

    private AccountRequest accountRequest;

    public AccountRestRequest(AccountRequest accountRequest) {
        super(AccountDTO.class);
        this.accountRequest = accountRequest;
    }

    @Override
    public AccountDTO loadDataFromNetwork() throws Exception {
        return getRestTemplate().postForObject(baseUrl+"/courier/getAccount", accountRequest, AccountDTO.class);
    }
}
