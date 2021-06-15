
       public class dataFile {
           private String name;
           private byte[] data;

           public dataFile(String name, byte[] data) {
               this.name = name;
               this.data = data;
           }

           public void setName(String name) {
               this.name = name;
           }

           public void setData(byte[] data) {
               this.data = data;
           }

           public String getName() {
               return name;
           }

           public byte[] getData() {
               return data;
           }
       }
