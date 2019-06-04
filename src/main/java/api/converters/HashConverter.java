package api.converters;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import utils.SHA3Util;

public class HashConverter implements Converter<byte[]> {
    @Override
    public void serialize(byte[] bytes, ObjectWriter objectWriter, Context context) throws Exception {
        objectWriter.writeString(SHA3Util.digestToHex(bytes));
    }

    @Override
    public byte[] deserialize(ObjectReader objectReader, Context context) throws Exception {
        return SHA3Util.hexToDigest(objectReader.valueAsString());
    }
}
