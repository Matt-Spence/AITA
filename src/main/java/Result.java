public class Result
{
    private String path, score, code, err, output;

    public Result (String path, String score, String code, String err, String output)
    {
        setPath(path);
        setCode(code);
        setScore(score);
        setErr(err);
        setOutput(output);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}