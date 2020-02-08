package com.solace.test;

import static java.lang.System.currentTimeMillis;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BulkSubscriberTest
{
    @Test
    public void directSubsTest() throws Exception
    {
        final int tcount = 100000;

        BulkSubscriber subscriber = new BulkSubscriber();
        long dur = subscriber.run( tcount );

        System.out.printf( "Created %d subscriptions in %d ms", tcount, dur );
    }
}
