package api.converters;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import utils.SHA3Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Converter to serialize and deserialize a list of objects
 */
public class HashListConverter implements Converter<List<byte[]>> {

    /**
     * Serializes a list of objects
     *
     * @param bytes        List of objects to serialize
     * @param objectWriter Writer to rite the serialized objects to
     * @param context      Context
     * @throws Exception Exception if something went's wrong with the serialization
     */
    @Override
    public void serialize(List<byte[]> bytes, ObjectWriter objectWriter, Context context) throws Exception {
        objectWriter.beginArray();
        for (byte[] aByte : bytes) {
            objectWriter.writeString(SHA3Util.digestToHex(aByte));
        }
        objectWriter.endArray();
    }

    /**
     * Deserializes a list of objects
     *
     * @param objectReader Reader to read the objects from
     * @param context      Context
     * @return List of deserialized objects
     * @throws Exception Exception if something went's wrong with the deserialization
     */
    @Override
    public List<byte[]> deserialize(ObjectReader objectReader, Context context) throws Exception {
        List<byte[]> hashes = new ArrayList<>();

        objectReader.beginArray();

        while (objectReader.hasNext()) {
            objectReader.next();
            hashes.add(SHA3Util.hexToDigest(objectReader.valueAsString()));
        }

        objectReader.endArray();

        return hashes;
    }
}
