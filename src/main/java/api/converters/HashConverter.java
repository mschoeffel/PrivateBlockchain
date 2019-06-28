package api.converters;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import utils.SHA3Util;

/**
 * Converter to deserialize and serialize an object
 */
public class HashConverter implements Converter<byte[]> {

    /**
     * Serializes an object
     *
     * @param bytes        Object to serialize
     * @param objectWriter Writer to write the serialized object to
     * @param context      Context
     * @throws Exception Exception if something went's wrong with the serialization
     */
    @Override
    public void serialize(byte[] bytes, ObjectWriter objectWriter, Context context) throws Exception {
        objectWriter.writeString(SHA3Util.digestToHex(bytes));
    }

    /**
     * Deserializes an object
     *
     * @param objectReader Reader to read from
     * @param context      Context
     * @return Deserialized  object
     * @throws Exception Exception if something went's wrong with the deserialization
     */
    @Override
    public byte[] deserialize(ObjectReader objectReader, Context context) throws Exception {
        return SHA3Util.hexToDigest(objectReader.valueAsString());
    }
}
