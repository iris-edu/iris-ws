package edu.iris.dmc.sacpz;

import edu.iris.dmc.sacpz.model.NumberUnit;
import edu.iris.dmc.sacpz.model.Pole;
import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.sacpz.model.Zero;
import edu.iris.dmc.ws.util.DateUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.CaseUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;

public class SacpzReader implements Closeable {
    private final BufferedReader reader;
    private final List<String> keys = Arrays.asList("NETWORK",
            "STATION",
            "LOCATION",
            "CHANNEL",
            "CREATED",
            "START",
            "END",
            "DESCRIPTION",
            "LATITUDE",
            "LONGITUDE",
            "ELEVATION",
            "DEPTH",
            "DIP",
            "AZIMUTH",
            "SAMPLE RATE",
            "INPUT UNIT",
            "OUTPUT UNIT",
            "INSTTYPE",
            "INSTGAIN",
            "COMMENT",
            "SENSITIVITY",
            "A0", "ZEROS", "POLES", "CONSTANT");
    public SacpzReader(InputStream inputStream){
        this(new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))));
    }

    public SacpzReader(Reader reader){
        Objects.requireNonNull(reader);
        if(!(reader instanceof BufferedReader)){
            this.reader=new BufferedReader(reader);
        }else{
            this.reader= (BufferedReader) reader;
        }
    }

    public Sacpz readRecord() throws IOException {
        try {
            Sacpz sacpz = null;
            for (String key : keys) {
                Pair<String, Object> pair = readKeyValue();
                if (pair == null) {
                    break;
                }
                if (!key.equals(pair.getKey())) {
                    throw new IOException("Expected "+key+" but received "+pair.getLeft());
                }
                if (sacpz == null) {
                    sacpz = new Sacpz();
                }
                Field field = sacpz.getClass()
                        .getDeclaredField(CaseUtils.toCamelCase(key.toLowerCase(), false, ','));
                field.setAccessible(true);
                if("poles".equalsIgnoreCase(key)){
                    field.set(sacpz, pair.getRight());
                }else if("zeros".equalsIgnoreCase(key)){
                    field.set(sacpz, pair.getRight());
                }else if(double.class.equals(field.getType())||field.getType().isAssignableFrom(Double.class)){
                    Object obj = pair.getRight();
                    if(pair.getRight()!=null){
                        if(pair.getRight() instanceof Double){
                            field.set(sacpz, obj);
                        }else{
                            field.set(sacpz, pair.getRight()==null?null:Double.parseDouble((String) obj));
                        }
                    }
                }else if(String.class.equals(field.getType())){
                    field.set(sacpz, pair.getRight());
                }else if(NumberUnit.class.equals(field.getType())){
                    String value = (String) pair.getRight();
                    if(value==null){
                        continue;
                    }
                    value=value.trim();
                    if(value.isEmpty()){
                        continue;
                    }
                    int i=0;
                    for(;i<value.length();i++){
                        char c = value.charAt(i);
                        if(c==' '){
                            break;
                        }
                    }
                    String number = value.substring(0, i).trim();
                    if(number.isEmpty()){
                        continue;
                    }
                    String unit = value.substring(i, value.length()).trim();
                    NumberUnit numberUnit=NumberUnit.builder().value(Double.parseDouble(number)).unit(unit.isEmpty()?null:unit).build();
                    field.set(sacpz, numberUnit);
                }else if(Date.class.equals(field.getType())){
                    field.set(sacpz, DateUtil.parseAny((String)pair.getRight()));
                }else{
                    throw new IOException("Unknown "+key+" type: "+field.getType());
                }
            }
            return sacpz;
        } catch (NoSuchFieldException | IllegalAccessException | ParseException e) {
            throw new IOException(e);
        }
    }

    Pair<String, Object> readKeyValue() throws IOException {
        while(true) {
            String line = readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if(line.startsWith("*")) {
                if(line.endsWith("*")){
                    continue;
                }
                line = line.substring(1).trim();
                if(line.isEmpty()){
                    continue;//may be throw exception
                }
                String[] array = line.split(":", 2);
                String key = array[0].trim();
                int index = key.indexOf('(');
                if(index>0){
                    key = key.substring(0, index).trim();
                }
                String value = null;
                if(array.length>1){
                    value = array[1].trim();
                }
                return Pair.of(key, value);
            }else{
                if(line.startsWith("POLES")){
                    List<Pole>poles = new ArrayList<>();
                    String[]array=line.split("\\s+");
                    if(array.length != 2){
                        throw new IOException();
                    }
                    int cnt = Integer.parseInt(array[1].trim());
                    for(int i=0;i<cnt;i++){
                        line = readLine();
                        if(line==null){
                            throw new IOException();
                        }
                        array = line.trim().split("\\s+");
                        poles.add(Pole.of(Double.parseDouble(array[0].trim()), Double.parseDouble(array[1].trim())));
                    }
                    return Pair.of("POLES", poles);
                }else if(line.startsWith("ZEROS")){
                    List<Zero>zeros = new ArrayList<>();
                    String[]array=line.split("\\s+");
                    if(array.length != 2){
                        throw new IOException();
                    }
                    int cnt = Integer.parseInt(array[1].trim());
                    for(int i=0;i<cnt;i++){
                        line = readLine();
                        if(line==null){
                            throw new IOException();
                        }
                        array = line.trim().split("\\s+");
                        zeros.add(Zero.of(Double.parseDouble(array[0].trim()), Double.parseDouble(array[1].trim())));
                    }
                    return Pair.of("ZEROS", zeros);
                }else if(line.startsWith("CONSTANT")){
                    String[]array=line.split("\\s+");
                    if(array.length != 2){
                        throw new IOException();
                    }

                    return Pair.of(array[0].trim(), Double.parseDouble(array[1].trim()));
                }
            }
        }
        return null;
    }

    String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
