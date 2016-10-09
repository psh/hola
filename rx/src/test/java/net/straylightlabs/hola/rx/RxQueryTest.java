package net.straylightlabs.hola.rx;

import net.straylightlabs.hola.dns.Domain;
import net.straylightlabs.hola.sd.Service;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RxQueryTest {
    @Test
    public void testChromecast() throws Exception {
        Service service = Service.fromName("_googlecast._tcp");
        RxQuery.createFor(service, Domain.LOCAL)
                .subscribeOn(Schedulers.newThread())
                .toList()
                .flatMap(Observable::from)
                .subscribe(System.err::println);
    }
}
