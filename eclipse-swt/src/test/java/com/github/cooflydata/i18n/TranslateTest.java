package com.github.cooflydata.i18n;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author 东来
 * @email 80871901@qq.com
 */
public class TranslateTest {

    @Test
    public void test1() {
        Translator translate = new Translator();
        Assert.assertEquals(translate.translate("not.not", "&View"), "视图");
    }

    @Test
    public void test2() {
        Translator translate = new Translator();
        Assert.assertEquals(translate.translate("org.eclipse.swt.custom.CTabItem", "Console"), "控制台");
    }

}
