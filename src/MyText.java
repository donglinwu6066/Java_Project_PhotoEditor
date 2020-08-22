import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

public class MyText{
    public Text text;
    public MyText(String text){
        this.text = new Text(text); 
        this.text.setTranslateX(100);
        this.text.setTranslateY(100);
    }
    public void setFontStyle(String family, boolean bold, boolean italic, int size){
        if(!bold && !italic){ this.text.setFont(Font.font (family, size));}
        else if(bold && italic){this.text.setFont(Font.font (family, FontWeight.BOLD, FontPosture.ITALIC , size));}
        else if(bold){this.text.setFont(Font.font (family, FontWeight.BOLD , size));}
        else{this.text.setFont(Font.font (family, FontPosture.ITALIC , size));}
    }
    public void setPosition(int x, int y){
        this.text.setTranslateX(x);
        this.text.setTranslateY(y);
    }
    public void setFill(Color c){
        this.text.setFill(c);
    }
    public void updateFontStyle(Text t, String family, boolean bold, boolean italic, int size){
        if(!bold && !italic){ t.setFont(Font.font (family, size));}
        else if(bold && italic){t.setFont(Font.font (family, FontWeight.BOLD, FontPosture.ITALIC , size));}
        else if(bold){t.setFont(Font.font (family, FontWeight.BOLD , size));}
        else{t.setFont(Font.font (family, FontPosture.ITALIC , size));}
    }

}