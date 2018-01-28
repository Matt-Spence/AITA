public class SearchString
{
    private String regex, orig;
    private int value;

    public SearchString(String regex, String orig, int value)
    {
        setRegex(regex);
        setOrig(orig);
        setValue(value);
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getOrig() {
        return orig;
    }

    public void setOrig(String orig) {
        this.orig = orig;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}