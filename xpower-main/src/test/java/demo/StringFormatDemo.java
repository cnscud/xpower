/**
 * 
 */
package demo;

/**
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2012-12-6
 */
public class StringFormatDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int m = Math.max("[update]".length(), "<old>".length());
        int n = Math.max("admin".length(), "==>".length());
        String f = "%<m>s %<n>s: %s\n%<m>s %<n>s: %s".replaceAll("<m>", ""+m).replaceAll("<n>", ""+n);
        String s = String.format(f,"[update]","admin","xfasfdddd  23rsdfa f23",//
                "<old>","==>","yyyyyyyyymmm233===sf23,32434");
        System.out.println(s);
    }

}
