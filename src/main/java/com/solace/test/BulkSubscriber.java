package com.solace.test;

import com.solacesystems.jcsmp.*;

import java.util.concurrent.CountDownLatch;

import static java.lang.System.currentTimeMillis;

public class BulkSubscriber
{
    final JCSMPProperties properties;
    final JCSMPSession session;
    final XMLMessageProducer prod;
    final XMLMessageConsumer cons;

    Topic[] makeTopics(int topicCount) {
        Topic[] topics = new Topic[ topicCount ];
        int i = 0, ntopics = 0;
        for(char a = 'a'; a <='z'; a++ ) {
            for(char b = 'a'; b <='z'; b++ ) {
                for(char c = 'a'; c <='z'; c++ ) {
                    for(char d = 'a'; d <='z'; d++ ) {
                        topics[i++] = JCSMPFactory.onlyInstance().createTopic( String.format("%c/%c/%c/%c", a,b,c,d) );
                        if ( ++ntopics >= topicCount ) {
                            return topics;
                        }
                    }
                }
            }
        }
        return topics;
    }

    public BulkSubscriber() throws JCSMPException {
        properties = new JCSMPProperties( );
        properties.setProperty( JCSMPProperties.HOST, "localhost"   );
        properties.setProperty( JCSMPProperties.VPN_NAME, "default" );
        properties.setProperty( JCSMPProperties.USERNAME, "default" );
        session = JCSMPFactory.onlyInstance().createSession( properties,
                JCSMPFactory.onlyInstance().getDefaultContext(),
                new SessionEventHandler() {
                    public void handleEvent(SessionEventArgs args) {
                    }
                } );
        prod = session.getMessageProducer(
                new JCSMPStreamingPublishEventHandler() {
                    public void handleError(String msgID, JCSMPException cause, long tstamp) {
                    }
                    public void responseReceived(String msgID) {
                    }
                },
                new JCSMPProducerEventHandler() {
                    public void handleEvent(ProducerEventArgs args) {
                    }
                }
        );
        cons = session.getMessageConsumer(
                new JCSMPReconnectEventHandler() {
                    public boolean preReconnect() throws JCSMPException { return true; }
                    public void postReconnect() throws JCSMPException { }
                },
                new XMLMessageListener() {
                    public void onReceive(BytesXMLMessage msg) { }
                    public void onException(JCSMPException e) { }
                }
        );
    }

    public long run(int topicCount) throws Exception {
        Topic[] topics = makeTopics( topicCount );

        int t = 0;
        long start = currentTimeMillis( );
        for( ; t < topicCount-1; t++ ) {
            session.addSubscription( topics[t], false );
        }
        session.addSubscription( topics[t], true );
        long span = currentTimeMillis() - start;
        cons.start();
        return span;
    }

    public void disconnect() {
        cons.close();
        session.closeSession();
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
