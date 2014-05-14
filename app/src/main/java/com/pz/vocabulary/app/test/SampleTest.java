package com.pz.vocabulary.app.test;

import junit.framework.TestCase;

/**
 * Created by piotr on 27.04.2014.
 */
public class SampleTest extends TestCase{

    public void testOne()
    {
        int a = 5;
        int b = 5;

        assertEquals(10, a+b);
    }
}
