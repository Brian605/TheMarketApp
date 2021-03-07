package com.returno.tradeit;

import com.returno.tradeit.utils.FirebaseUtils;
import com.returno.tradeit.utils.ItemUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void assert_date(){
        FirebaseUtils utils=new FirebaseUtils();

        //assertEquals("6",utils.getDateDifference("28/06/2020"));
        System.out.println(utils.getDateDifference("28/08/2020"));
    }

    @Test
    public void check_generated_uuid(){
        System.out.println(ItemUtils.generateItemId());
    }


}