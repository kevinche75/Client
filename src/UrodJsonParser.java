import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;


public class UrodJsonParser {



    public CopyOnWriteArrayList<Alice> getArraysofAliceObjects(String rawarrays){
        CopyOnWriteArrayList<Alice> arrayList = new CopyOnWriteArrayList<>();
        String str = "\\}[, \n\r]*\\{";
        String [] array = rawarrays.split(str);
        int i =0;
            if (array.length > 1) {
                i=1;
                array[0] = array[0] + "}";
                try{
                    arrayList.add(simpleParseAliceObjects(array[0]));
                }catch (JsonException e){
                    System.out.println("===\nОбнаружена ошибка при парсинге элемента № " + 1 + e.getMessage());
                }
                array[array.length - 1] = "{" + array[array.length - 1];
                try{
                    arrayList.add(simpleParseAliceObjects(array[array.length - 1]));
                }catch (JsonException e){
                    System.out.println("===\nОбнаружена ошибка при парсинге элемента № " + array.length + e.getMessage());
                }
                for (i = 1; i < array.length - 1; i++) {
                    array[i] = "{" + array[i] + "}";
                    try{
                        arrayList.add(simpleParseAliceObjects(array[i]));
                    }catch (JsonException e){
                        System.out.println("===\nОбнаружена ошибка при парсинге элемента № " + new Integer(i+1) + e.getMessage());
                    }
                }
            } else {
                try{
                    arrayList.add(simpleParseAliceObjects(array[0]));
                }catch (JsonException e){
                    System.out.println("===\nОбнаружена ошибка при парсинге элемента № " + 0 + e.getMessage());
                }
            }
        return arrayList;
    }

    public Alice simpleParseAliceObjects(String rawjson)throws JsonException{
        boolean politeness = false;
        boolean condition = false;
        boolean cap = false;
        boolean nameOfUser = false;
        boolean name = false;
        boolean x =false;
        boolean fullness = false;
        boolean date = false;
        boolean size = false;
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Politeness politeness_characteristic = null;
        String nameOfUser_characteristic  = null;
        String name_characteristic  = null;
        String date_characteristic = null;
        int size1 = 0;
        Date date1 = null;
        int fullness1 = 0;
        int x1=0;
        while(rawjson.contains("\"")){
            rawjson = rawjson.substring(rawjson.indexOf("\"")+1);
            int beginner = rawjson.indexOf("\"");
            switch (rawjson.substring(0,beginner)){
                case "politeness":
                    rawjson = rawjson.substring(beginner+1);
                    if(!(rawjson.indexOf(':')==0)|(!rawjson.contains("\"")))throw new JsonException("politeness (нет \':\' или значения)");
                    else {
                        rawjson =rawjson.substring(rawjson.indexOf("\"")+1);
                        switch (rawjson.substring(0,rawjson.indexOf("\""))){
                            case "POLITE":
                                politeness_characteristic = Politeness.POLITE;
                                politeness = true;
                                rawjson = rawjson.substring(rawjson.indexOf("\"")+1);
                                break;
                            case "RUDE":
                                politeness_characteristic = Politeness.RUDE;
                                politeness = true;
                                rawjson = rawjson.substring(rawjson.indexOf("\"")+1);
                                break;
                            default:
                                throw new JsonException("politeness (нет значения или оно указано неверно)");
                        }
                    }
                    break;
                case "condition":
                    rawjson =rawjson.substring(beginner+1);
                    if(!(rawjson.indexOf(':')==0)|!rawjson.contains("\""))throw new JsonException("condition (нет \':\' или значения)");
                    else {
                        rawjson =rawjson.substring(rawjson.indexOf("\"")+1);
                        switch (rawjson.substring(0,rawjson.indexOf("\""))){
                            case "NORMAL":
                                condition = true;
                                rawjson = rawjson.substring(rawjson.indexOf("\"")+1);
                                break;
                            default:
                                throw new JsonException("condition (нет значения или оно указано неверно)");
                        }
                    }
                    break;
                case "cap":
                    cap = true;
                    rawjson =rawjson.substring(beginner+1);
                    if((!(rawjson.indexOf(':')==0))|!(rawjson.contains("{"))|!(rawjson.contains("\"")))throw new JsonException("cap (нет \':\' или значения)");
                    else {
                        rawjson = rawjson.substring(rawjson.indexOf("\"") + 1);
                        String sstr = rawjson.substring(0, rawjson.indexOf("}")+1);
                        while (sstr.contains("\"")){
                            switch (rawjson.substring(0, rawjson.indexOf("\""))) {
                                case "fullness":
                                    String somestring = null;
                                    rawjson = rawjson.substring(rawjson.indexOf("\"") + 1);
                                    sstr = sstr.substring(sstr.indexOf("\"") + 1);
                                    if (!(rawjson.indexOf(':') == 0) | !(rawjson.contains("\"") | rawjson.contains("}")))
                                        throw new JsonException("fullness (нет \':\' или значения)");
                                    else {
                                        if (sstr.contains("\"")) {
                                            somestring = rawjson.substring(0, rawjson.indexOf("\""));
                                        } else {
                                            if (sstr.contains("}")) {
                                                somestring = rawjson.substring(0, rawjson.indexOf("}"));
                                            } else throw new JsonException("fullness (неправильно указано значение)");
                                        }
                                        somestring = somestring.replaceAll("[^0-9]", "");
                                        try {
                                            fullness1 = Integer.parseInt(somestring);
                                        } catch (NumberFormatException e) {
                                            throw new JsonException("fullness (неправильно указано значение)");
                                        }
                                    }
                                    fullness = true;
                                    if(nameOfUser){
                                        rawjson=rawjson.substring(rawjson.indexOf("}"));
                                        sstr=sstr.substring(sstr.indexOf("}"));
                                    }else{
                                        rawjson = rawjson.substring(rawjson.indexOf("\"") + 1);
                                        sstr = sstr.substring(sstr.indexOf("\"") + 1);
                                    }
                                    break;
                                case "nameOfUser":
                                    rawjson = rawjson.substring(rawjson.indexOf("\"") + 1);
                                    sstr = sstr.substring(sstr.indexOf("\"") + 1);
                                    if (!(rawjson.indexOf(':') == 0) | !rawjson.contains("\""))
                                        throw new JsonException("nameOfUser (нет \':\' или значения)");
                                    rawjson = rawjson.substring(rawjson.indexOf("\"") + 1);
                                    sstr = sstr.substring(sstr.indexOf("\"") + 1);
                                    nameOfUser = true;
                                    nameOfUser_characteristic = rawjson.substring(0, rawjson.indexOf("\""));
                                    if (nameOfUser_characteristic.matches(".*[0-9].*")) {
                                        throw new JsonException("name (имя содержит цифры)");
                                    }
                                    rawjson = rawjson.substring(rawjson.indexOf("\"") + 1);
                                    sstr = sstr.substring(sstr.indexOf("\"") + 1);
                                    if(!fullness){
                                        rawjson = rawjson.substring(rawjson.indexOf("\"") + 1);
                                        sstr = sstr.substring(sstr.indexOf("\"") + 1);
                                    }
                                    break;
                                default:
                                    throw new JsonException("cap (неправильно указано поле)");
                            }
                        }
                    }
//                               if(!rawjson.substring(0, rawjson.indexOf("\"")).equals("nameOfUser"))throw new JsonException("cap (должно быть поле nameOfUser)");

                    break;
                case "name":
                    rawjson =rawjson.substring(beginner+1);
                    if(!(rawjson.indexOf(':')==0)|!rawjson.contains("\""))throw new JsonException("name (нет \':\' или значения)");
                    else {
                        rawjson = rawjson.substring(rawjson.indexOf("\"") + 1);
                        name = true;
                        name_characteristic  = rawjson.substring(0,rawjson.indexOf("\""));
                        if (name_characteristic.matches(".*[0-9].*")){
                            throw new JsonException("name (имя содержит цифры)");
                        }
                        rawjson = rawjson.substring(rawjson.indexOf("\"")+1);
                    }
                    break;
                case "date":
                    rawjson =rawjson.substring(beginner+1);
                    if(!(rawjson.indexOf(':')==0)|!rawjson.contains("\""))throw new JsonException("date (нет \':\' или значения)");
                    else {
                        rawjson = rawjson.substring(rawjson.indexOf("\"") + 1);
                        date = true;
                        date_characteristic  = rawjson.substring(0,rawjson.indexOf("\""));
                        try {
                            date1 = format.parse(date_characteristic);
                        } catch (ParseException e) {
                            throw new JsonException("date (неправильно указано значение)");
                        }
                        rawjson = rawjson.substring(rawjson.indexOf("\"")+1);
                    }
                    break;
                case "x":
                    String somestring = null;
                    rawjson =rawjson.substring(beginner+1);
                    if(!(rawjson.indexOf(':')==0)|!(rawjson.contains("\"")|rawjson.contains("}")))throw new JsonException("x (нет \':\' или значения)");
                    else{
                        if(rawjson.contains("\"")){
                            somestring = rawjson.substring(0,rawjson.indexOf("\""));
                        } else {
                            if(rawjson.contains("}")){
                                somestring = rawjson.substring(0,rawjson.indexOf("}"));
                            }
                        }
                        somestring = somestring.replaceAll("[^0-9]", "");
                        try{
                            x1 = Integer.parseInt(somestring);
                        }catch (NumberFormatException e){
                            throw new JsonException("x (неправильно указано значение)");
                        }
                    }
                    x = true;
                    break;
                case "size":
                    String string = null;
                    rawjson =rawjson.substring(beginner+1);
                    if(!(rawjson.indexOf(':')==0)|!(rawjson.contains("\"")|rawjson.contains("}")))throw new JsonException("x (нет \':\' или значения)");
                    else{
                        if(rawjson.contains("\"")){
                            string = rawjson.substring(0,rawjson.indexOf("\""));
                        } else {
                            if(rawjson.contains("}")){
                                string = rawjson.substring(0,rawjson.indexOf("}"));
                            }
                        }
                        somestring = string.replaceAll("[^0-9]", "");
                        try{
                            size1 = Integer.parseInt(somestring);
                        }catch (NumberFormatException e){
                            throw new JsonException("size (неправильно указано значение)");
                        }
                    }
                    size = true;
                    break;
                default:
                    throw new JsonException("недопустимое поле");
            }
        }
        if(!politeness) throw new JsonException("поле politeness отсутствует");
        if(!condition) throw new JsonException("поле condition отсутствует");
        if(!cap) throw new JsonException("поле cap отсутствует");
        if(!nameOfUser) throw new JsonException("поле nameOfuser отсутствует");
        if(!name) throw new JsonException("поле name отсутствует");
        if(!x)throw new JsonException("поле x отсутствует");
        if(!fullness) throw new JsonException("поле fullness отсутствует");
        if(!name_characteristic .equals(nameOfUser_characteristic )) throw new JsonException("обнаружено, что name и nameOfUser не совпадает");
        if(!date) throw new JsonException("поле date отсутствует");
        if(!size) throw new JsonException("поле size отсутствует");
        return new Alice(name_characteristic ,politeness_characteristic,x1,fullness1,date_characteristic,size1);
    }

    private String getAliceInString(Alice aliceforwriting){
        return "{\n  \"politeness\": \"" + aliceforwriting.getPoliteness() + "\",\n  \"date\": " +aliceforwriting.getDate()+ "\",\n  \"size\": " +aliceforwriting.getSize() + "\",\n  \"condition\": \"NORMAL\",\n  \"cap\": {\n  \t\"nameOfUser\": \"" + aliceforwriting.getName() + "\"\n\t\"fullness\": " + aliceforwriting.getfullness() + "\n  },\n  \"name\": \""+aliceforwriting.getName()+"\",\n  \"x\": " + aliceforwriting.getLocation() + "\n  }";
         }

    public String getWrittenAlices (CopyOnWriteArrayList<Alice> linkedList){
        String buildingstring = "[\n";
        if(linkedList.size()>0) {
        Iterator <Alice> iterator = linkedList.iterator();
             for(int i =0; i < linkedList.size()-1;i++){
                buildingstring = buildingstring + getAliceInString(iterator.next())+",\n";
             }
             buildingstring = buildingstring + getAliceInString(linkedList.get(linkedList.size()-1));
        }
             return buildingstring + "\n]";
         }
        }