import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;

public class MyImage {
    private Image image;
    public ImageView imageView;

    public MyImage() {                
    }
    public MyImage(Image image) {        
        this.image = image;
        this.imageView = new ImageView(image);
        // this.imageView.setImage(image);
        //setResizableTool(imageView);       
        //imageView.setPreserveRatio(true); 
        setSize();
    }
    public void setImage(Image image){
        this.image = image;
        this.imageView = new ImageView(image);
        // this.imageView.setImage(image);
        //setResizableTool(imageView);  
        //imageView.setPreserveRatio(true);   
        setSize();
    }
    public void setSize(){
        
        if(this.image.getWidth()>1297.6 || this.image.getHeight()>708){
            if(this.imageView.getFitWidth()/1297.6 > this.imageView.getFitHeight()/708){
                this.imageView.setFitHeight(this.image.getHeight()*(1297.6/this.image.getWidth())) ; 
                this.imageView.setFitWidth(1297.6);} 
            else{
                this.imageView.setFitWidth(this.image.getWidth()*(708/this.image.getHeight())); 
                this.imageView.setFitHeight(708);}
        }
        // System.out.printf("hi %f %f", this.imageView.getFitWidth(), this.imageView.getFitHeight());
    }
    public void setPosition(double x, double y) {
        imageView.setLayoutX(x - image.getWidth() / 2);
        imageView.setLayoutY(y - image.getHeight() / 2);
    }
    
    public Image getImage(){
        return image;
    }
    public ImageView getImageView(){
        return imageView;
    }
}