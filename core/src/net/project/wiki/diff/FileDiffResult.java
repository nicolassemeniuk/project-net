package net.project.wiki.diff;

public class FileDiffResult {

    private ParsedFile leftFile, rightFile;

    public FileDiffResult(ParsedFile leftFile, ParsedFile rightFile) {
        this.leftFile = leftFile;
        this.rightFile = rightFile;
    }

    public ParsedFile getLeftFile(){
        return leftFile;
    }

    public ParsedFile getRightFile(){
        return rightFile;
    }
}
