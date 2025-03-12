package ru.skillbox.currency.exchange.dto;

import lombok.Getter;
import lombok.Setter;
import ru.skillbox.currency.exchange.jaxb.CurrencyJaxb;

import javax.xml.bind.annotation.*;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ValCurs")
public class ValCurs {

    @XmlAttribute(name = "Date")
    private String date;

    @XmlAttribute(name = "name")
    private String name;

    @Setter
    @Getter
    @XmlElement(name = "Valute")
    private List<CurrencyJaxb> currencies;
}