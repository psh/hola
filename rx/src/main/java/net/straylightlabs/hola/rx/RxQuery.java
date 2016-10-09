package net.straylightlabs.hola.rx;

import net.straylightlabs.hola.dns.Domain;
import net.straylightlabs.hola.sd.Instance;
import net.straylightlabs.hola.sd.Query;
import net.straylightlabs.hola.sd.Service;
import rx.Observable;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class RxQuery {
    public static Observable<Instance> createFor(Service service, Domain domain) {
        try {
            return enumerate(NetworkInterface.getNetworkInterfaces())
                    .filter(ni -> {
                        try {
                            return !ni.isVirtual() && !ni.isLoopback() && ni.isUp();
                        } catch (Exception ignored) {
                            return false;
                        }
                    })
                    .flatMap(ni -> enumerate(ni.getInetAddresses()))
                    .flatMap(address -> {
                        try {
                            return Observable.from(Query.createFor(service, domain).runOnceOn(address));
                        } catch (IOException ignored) {
                            return Observable.empty();
                        }
                    })
                    .distinct();
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    private static <T> Observable<T> enumerate(final Enumeration<T> en) {
        return Observable.create(subscriber -> {
            while (en.hasMoreElements()) {
                subscriber.onNext(en.nextElement());
            }
            subscriber.onCompleted();
        });
    }
}
