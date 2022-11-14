package com.lab2.airlinereservationsystem.utils;

import com.lab2.airlinereservationsystem.common.exception.ErrorExceptionWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
/**
 * @Arthor Yikang Chen, Qiong Wu
 * Utils function for xml format
 */
public class XmlUtil {

    private XmlUtil() {
        throw new IllegalStateException("XmlUtil class");
    }

    public static String convertObject2Xml(Object object){
        try {
            JAXBContext context= JAXBContext.newInstance(object.getClass());
            Marshaller marshaller=context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING,"UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new ErrorExceptionWrapper("convert xml error");
        }
    }
}
