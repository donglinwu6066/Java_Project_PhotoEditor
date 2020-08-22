import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MyScrollPane extends ScrollPane{
    private Image []scrollPaneProcessedImage;
    private ImageView []scrollPaneImageView;
    private int number_Of_Image;
    private MyImage processedImageView;
    private MyFilter myFilter;

    public MyScrollPane(Image []scrollPaneProcessedImage,int number_Of_Image,MyFilter myFilter){
        this.scrollPaneProcessedImage=scrollPaneProcessedImage;
        this.number_Of_Image=number_Of_Image;
        // this.processedImageView=processedImageView;
        this.myFilter=myFilter;
        scrollPaneImageView=new ImageView[number_Of_Image];
        
        VBox vbox = new VBox();

        for(int i=0;i<number_Of_Image;i++){
            scrollPaneImageView[i]=new ImageView( scrollPaneProcessedImage[i] );
            scrollPaneImageView[i].setFitWidth(90);
            scrollPaneImageView[i].setFitHeight(90);
            scrollPaneImageView[i].addEventFilter( MouseEvent.MOUSE_PRESSED, getOnMousePressedEventHandler(i));
            // this.getChildren().add(scrollPaneImageView[i]);
            vbox.getChildren().add(scrollPaneImageView[i]);
        }
        this.setContent(vbox);
        //System.out.println("*");
    }


    public EventHandler<MouseEvent> getOnMousePressedEventHandler(int index) {
        //return onMousePressedEventHandler;
        return( 
            new EventHandler<MouseEvent>() {

                public void handle(MouseEvent event) {
        
                    // left mouse button => dragging
                    if( event.isPrimaryButtonDown()){
                        // processedImageView.imageView.setImage(scrollPaneProcessedImage[index]);
                        myFilter.setMyFilter(scrollPaneProcessedImage[index]);
                        //System.out.println("x");
                        
                    }
                }
    
            }
        );
    }
    
    /*private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent event) {

            // left mouse button => dragging
            if( event.isPrimaryButtonDown()){
                processedImageView.imageView.setImage();
                //System.out.println("x");
                
            }
        }

    };*/
}

