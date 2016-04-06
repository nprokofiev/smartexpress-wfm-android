package ru.smartexpress.courierapp.test;

import org.junit.Assert;
import org.junit.Test;
import ru.smartexpress.courierapp.helper.ValidationHelper;

import java.util.regex.Pattern;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 01.04.16 14:40
 */

public class SimpleTest {
    @Test
    public void testPhone(){
        Assert.assertTrue(ValidationHelper.isPhoneValid("79268365045"));
        Assert.assertFalse(ValidationHelper.isPhoneValid("89268365045"));
        Assert.assertFalse(ValidationHelper.isPhoneValid("8765"));
    }
}
