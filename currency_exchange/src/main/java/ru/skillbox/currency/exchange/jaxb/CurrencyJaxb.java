package ru.skillbox.currency.exchange.jaxb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "Valute")
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrencyJaxb {

    @XmlAttribute(name = "ID")
    private String id;

    @XmlElement(name = "NumCode")
    private int isoNumCode;

    @XmlElement(name = "CharCode")
    private String isoCharCode;

    @XmlElement(name = "Nominal")
    private int nominal;

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "Value")
    private String valueStr;

    public BigDecimal getValue() {
        return new BigDecimal(valueStr.replace(",", "."));
    }
}
