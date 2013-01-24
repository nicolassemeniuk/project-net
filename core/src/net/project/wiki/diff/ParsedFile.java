package net.project.wiki.diff;

import java.util.ArrayList;

public class ParsedFile {

    private FileLine lines[];

    public ParsedFile(String fileContent) {
        String[] fileLines = fileContent.split("\r\n|\n|\r");
        lines = new FileLine[fileLines.length];
        for(int i=0;i<fileLines.length;i++){
            lines[i] = new FileLine(fileLines[i],i);
        }
    }

    public ParsedFile(ArrayList lineArray){
        lines = new FileLine[lineArray.size()];
        for(int i=0;i<lineArray.size();i++){
            lines[i] = (FileLine)lineArray.get(i);
        }
    }

    public ParsedFile(FileLine lines[]){
      this.lines = lines;
    }

    public FileLine[] getLines(){
        return lines;
    }
}
