
import javafx.scene.image.Image;  
import javafx.scene.image.ImageView; 
import javafx.scene.image.PixelReader; 
import javafx.scene.image.PixelWriter; 
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.Arrays;

public class MyFilter{
    Image image;
    
    public MyFilter(){
    }
    public MyFilter(Image image){
        this.image = image;
    }
    public void setMyFilter(Image image){
        this.image = image;
    }
    public Filter_None getFilter_None(){
        return new Filter_None(image);
    }
    public Filter_Alpha getFilter_Alpha(int alpha_Degree){
        return new Filter_Alpha(image,alpha_Degree);
    }
    public Filter_Negative getFilter_Negative(){
        return new Filter_Negative(image);
    }
    public Filter_EdgeDetecting getFilter_EdgeDetecting(){
        return new Filter_EdgeDetecting(image);
    }
    public Filter_GrayScale getFilter_GrayScale(){
        return new Filter_GrayScale(image);
    }
    public Filter_Bluring getFilter_Bluring(int extent){
        return new Filter_Bluring(image,extent);
    }
    public Filter_Brightness getFilter_Brightness(int brightness_Degree){
        return new Filter_Brightness(image,brightness_Degree);
    }
    public Filter_Median getFilter_Median(){
        return new Filter_Median(image);
    }
    public Filter_Mirror getFilter_Mirror(){
        return new Filter_Mirror(image);
    }
    public Filter_Sharpen getFilter_Sharpen(){
        return new Filter_Sharpen(image);
    }
    public Filter_Emboss getFilter_Emboss(){
        return new Filter_Emboss(image);
    }
}
//-----------------------------------------------
class Filter {
    public Image image;
    public WritableImage writableImage;
    public PixelReader pixelReader;
    public PixelWriter pixelWriter;
    public int widthOfImage,heightOfImage;
    public int choice;
    public int a,r,g,b,p;

    public void imageSetting(){
        widthOfImage = (int)image.getWidth(); 
        heightOfImage = (int)image.getHeight();
        //Creating a writable image 
        writableImage = new WritableImage(widthOfImage, heightOfImage); 
        //Reading color from the loaded image 
        pixelReader = image.getPixelReader(); 
        //getting the pixel writer 
        pixelWriter = writableImage.getPixelWriter(); 
    }

    public WritableImage getImageView(){
        return writableImage;
    }
}
//-----------------------------------------------
class Filter_None extends Filter{
    
    public Filter_None(Image image){
        this.image = image;
        imageSetting();
    }

    public Image getImage(){
        return image;
    }
        
}
//-----------------------------------------------

class Filter_Alpha extends Filter{
    
    private int alpha_Degree;
    public Filter_Alpha(Image image,int alpha_Degree){
        this.alpha_Degree=alpha_Degree;
        this.image = image;
        imageSetting();
        // p = pixelReader.getArgb(20, 20); 
        // a = (p>>24)&0xff; 
        System.out.printf("%d%n",alpha_Degree);
        //brightness_Degree=50;
        for(int x = 0; x < widthOfImage; x++) { 
            for(int y = 0; y < heightOfImage; y++) { 
                p = pixelReader.getArgb(x, y); 
                a = (p>>24)&0xff; 
                r = (p>>16)&0xff; 
                g = (p>>8)&0xff; 
                b = p&0xff; 
  
                a=alpha_Degree;
                
                p = (a<<24) | (r<<16) | (g<<8) | b; 
                pixelWriter.setArgb(x, y, p);       
                
                
            }
        }
    }
        
}
//-----------------------------------------------
class Filter_Bluring extends Filter{
    private int extent;
    private class ValueOfARGB{
        public int a,r,g,b;
    }
    private ValueOfARGB [][]array;
    

    public Filter_Bluring(Image image,int extent){
        this.extent=extent;
        this.image = image;
        imageSetting();
        
        array=new ValueOfARGB [widthOfImage][heightOfImage];
        for(int i=0;i<widthOfImage;i++)
            for(int j=0;j<heightOfImage;j++)    
                array[i][j]=new ValueOfARGB();
        
        for(int x = 0; x < widthOfImage; x++){//get ARGB
            for(int y = 0; y < heightOfImage; y++){
                p = pixelReader.getArgb(x, y); 
                a = (p>>24)&0xff; 
                r = (p>>16)&0xff; 
                g = (p>>8)&0xff; 
                b = p&0xff;
                
                array[x][y].a=a;
                array[x][y].r=r;
                array[x][y].g=g;
                array[x][y].b=b;
                
            }
        }

        for(int x = extent; x < widthOfImage-extent; x++) { //get new ARGB
            for(int y = extent; y < heightOfImage-extent; y++) { 
                bluringScale(x,y);
                pixelWriter.setArgb(x, y, p);        
            }
        }
    }
 
    public void bluringScale(int x,int y) {
        a =array[x][y].a;
        r=0;
        g=0;
        b=0;
        for(int i=x-extent;i<=x+extent;i++)
            for(int j=y-extent;j<=y+extent;j++){
                //System.out.printf("%d%n",x);
                r += array[i][j].r;
                g += array[i][j].g;
                b += array[i][j].b;
            }
        r=(int)r/((extent*2+1)*(extent*2+1));
        g=(int)g/((extent*2+1)*(extent*2+1));
        b=(int)b/((extent*2+1)*(extent*2+1));
        p = (a<<24) | (r<<16) | (g<<8) | b; 
    }
}
//-----------------------------------------------
class Filter_Brightness extends Filter{
    
    private int brightness_Degree;
    public Filter_Brightness(Image image,int brightness_Degree){
        this.brightness_Degree=brightness_Degree;
        this.image = image;
        imageSetting();

        //brightness_Degree=50;
        for(int x = 0; x < widthOfImage; x++) { 
            for(int y = 0; y < heightOfImage; y++) { 
                p = pixelReader.getArgb(x, y); 
                a = (p>>24)&0xff; 
                r = (p>>16)&0xff; 
                g = (p>>8)&0xff; 
                b = p&0xff; 
  
                //subtract RGB from 255 
                r += (int)(brightness_Degree*r)/100; 
                g += (int)(brightness_Degree*g)/100; 
                b += (int)(brightness_Degree*b)/100;
                // r=(int)(r*1.2); 
                // g=(int)(g*1.2); 
                // b=(int)(b*1.2); 
                r=Math.min(Math.max(0,r),255);
                g=Math.min(Math.max(0,g),255);
                b=Math.min(Math.max(0,b),255);
  
                //set new RGB value 
                p = (a<<24) | (r<<16) | (g<<8) | b; 
                pixelWriter.setArgb(x, y, p);       
                
                
            }
        }
    }
        
}
//-----------------------------------------------
class Filter_EdgeDetecting extends Filter{
    
    
    private int maxGval = 0;
    private int[][] edgeColors;
    private int maxGradient = -1;

    public Filter_EdgeDetecting(Image image){
        this.image = image;
        imageSetting();
        edgeColors = new int[widthOfImage][heightOfImage];
        for(int x = 1; x < widthOfImage-1; x++) { 
            for(int y = 1; y < heightOfImage-1; y++) { 
                int val00 = getGrayScale(pixelReader.getArgb(x - 1, y - 1));
                int val01 = getGrayScale(pixelReader.getArgb(x - 1, y));
                int val02 = getGrayScale(pixelReader.getArgb(x - 1, y + 1));
                int val10 = getGrayScale(pixelReader.getArgb(x, y - 1));
                int val11 = getGrayScale(pixelReader.getArgb(x, y));
                int val12 = getGrayScale(pixelReader.getArgb(x, y + 1));
                int val20 = getGrayScale(pixelReader.getArgb(x + 1, y - 1));
                int val21 = getGrayScale(pixelReader.getArgb(x + 1, y));
                int val22 = getGrayScale(pixelReader.getArgb(x + 1, y + 1));   
                
                int gx = ((-1 * val00) + (0 * val01) + (1 * val02))
                        + ((-2 * val10) + (0 * val11) + (2 * val12))
                        + ((-1 * val20) + (0 * val21) + (1 * val22));
                int gy = ((-1 * val00) + (-2 * val01) + (-1 * val02))
                        + ((0 * val10) + (0 * val11) + (0 * val12))
                        + ((1 * val20) + (2 * val21) + (1 * val22));
                        
                double gval = Math.sqrt((gx * gx) + (gy * gy));
                //System.out.printf("%d%n",c);
                //c++;
                int g = (int) gval;
                if(maxGradient < g) {
                    maxGradient = g;
                }

                edgeColors[x][y] = g;
                
            }
        }

        double scale = 255.0 / maxGradient;
        for (int x = 1; x < widthOfImage - 1; x++) {
            for (int y = 1; y < heightOfImage - 1; y++) {
                int edgeColor = edgeColors[x][y];
                edgeColor = (int)(edgeColor * scale);
                edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;
                pixelWriter.setArgb(x, y, edgeColor);
            }
        }


    }

    public int getGrayScale(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;
        //from https://en.wikipedia.org/wiki/Grayscale, calculating luminance
        int gray = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b);
        //int gray = (r + g + b) / 3;
        return gray;
    }
}
//-----------------------------------------------
class Filter_Emboss extends Filter{
    
    private class ValueOfARGB{
        public int a,r,g,b;
    }
    private ValueOfARGB [][]array;
    

    public Filter_Emboss(Image image){
        this.image = image;
        imageSetting();
        
        array=new ValueOfARGB [widthOfImage][heightOfImage];
        for(int i=0;i<widthOfImage;i++)
            for(int j=0;j<heightOfImage;j++)    
                array[i][j]=new ValueOfARGB();
        
        for(int x = 0; x < widthOfImage; x++){//get ARGB
            for(int y = 0; y < heightOfImage; y++){
                p = pixelReader.getArgb(x, y); 
                a = (p>>24)&0xff; 
                r = (p>>16)&0xff; 
                g = (p>>8)&0xff; 
                b = p&0xff;
                
                array[x][y].a=a;
                array[x][y].r=r;
                array[x][y].g=g;
                array[x][y].b=b;
                
            }
        }

        for(int x = 1; x < widthOfImage-1; x++) { //get new ARGB
            for(int y = 1; y < heightOfImage-1; y++) { 
                Sharping(x,y);
                pixelWriter.setArgb(x, y, p);        
            }
        }
    }
 

    public void Sharping(int x,int y) {
        a =array[x][y].a;
        r=0;
        g=0;
        b=0;
        //int [][]kernal={{0,-1,0},{-1,5,-1},{0,-1,0}};
        //System.out.println("&");
        /*for(int i=x-1;i<=x+1;i++){
            for(int j=y-1;j<=y+1;j++){
                r+=array[i][j].r*kernal[j-x+1][i-y+1];
                g+=array[i][j].g*kernal[j-x+1][i-y+1];
                b+=array[i][j].b*kernal[j-x+1][i-y+1];
            }
        }*/
        
        r += array[x-1][y-1].r*(-2); g += array[x-1][y-1].g*(-2); b += array[x-1][y-1].b*(-2);
        r += array[x-1][y].r*(-1);   g += array[x-1][y].g*(-1);   b += array[x-1][y].b*(-1);
        r += array[x-1][y+1].r*(0);  g += array[x-1][y+1].g*(0);  b += array[x-1][y+1].b*(0);
        r += array[x][y-1].r*(-1);   g += array[x][y-1].g*(-1);   b += array[x][y-1].b*(-1);
        r += array[x][y].r*(1);      g += array[x][y].g*(1);      b += array[x][y].b*(1);
        r += array[x][y+1].r*(1);    g += array[x][y+1].g*(1);    b += array[x][y+1].b*(1);
        r += array[x+1][y-1].r*(0);  g += array[x+1][y-1].g*(0);  b += array[x+1][y-1].b*(0);
        r += array[x+1][y].r*(1);    g += array[x+1][y].g*(1);    b += array[x+1][y].b*(1);
        r += array[x+1][y+1].r*(2);  g += array[x+1][y+1].g*(2);  b += array[x+1][y+1].b*(2);
        r=Math.min(Math.max(0,r),255);
        g=Math.min(Math.max(0,g),255);
        b=Math.min(Math.max(0,b),255);

        p = (a<<24) | (r<<16) | (g<<8) | b; 
    }
}
//-----------------------------------------------
class Filter_GrayScale extends Filter{
    

    public Filter_GrayScale(Image image){
        this.image = image;
        imageSetting();
        for(int x = 0; x < widthOfImage; x++) { 
            for(int y = 0; y < heightOfImage; y++) { 
                p = pixelReader.getArgb(x, y); 
                a = (p>>24)&0xff; 
                r = (p>>16)&0xff; 
                g = (p>>8)&0xff; 
                b = p&0xff; 
  
                //subtract RGB from 255 
                r = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b); 
                g = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b); 
                b = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b) ; 
  
                //set new RGB value 
                p = (a<<24) | (r<<16) | (g<<8) | b; 
                pixelWriter.setArgb(x, y, p);       
                
                
            }
        }
    }
        
}
//-----------------------------------------------
class Filter_Median extends Filter{
    
    private class ValueOfARGB{
        public int a,r,g,b;
    }
    private ValueOfARGB [][]array;
    

    public Filter_Median(Image image){
        this.image = image;
        imageSetting();
        
        array=new ValueOfARGB [widthOfImage][heightOfImage];
        for(int i=0;i<widthOfImage;i++)
            for(int j=0;j<heightOfImage;j++)    
                array[i][j]=new ValueOfARGB();
        
        for(int x = 0; x < widthOfImage; x++){//get ARGB
            for(int y = 0; y < heightOfImage; y++){
                p = pixelReader.getArgb(x, y); 
                a = (p>>24)&0xff; 
                r = (p>>16)&0xff; 
                g = (p>>8)&0xff; 
                b = p&0xff;
                
                array[x][y].a=a;
                array[x][y].r=r;
                array[x][y].g=g;
                array[x][y].b=b;
                
            }
        }

        for(int x=1; x < widthOfImage-1; x++) { //set new ARGB
            for(int y=1; y < heightOfImage-1; y++) { 
                setMedianValue(x,y);
                pixelWriter.setArgb(x, y, p);        
            }
        }
    }
 

    public void setMedianValue(int x,int y) {//set medians of RGB
        int []red=new int[9];
        int []green=new int[9];
        int []blue=new int[9];
        int index=0;
        a=array[x][y].a;
        r=0;
        g=0;
        b=0;
        for(int i=x-1;i<=x+1;i++)
            for(int j=y-1;j<=y+1;j++){
                //System.out.printf("%d%n",x);
                red[index]=array[i][j].r;
                green[index]=array[i][j].g;
                blue[index]=array[i][j].b;
                index++;
            }
        
        Arrays.sort(red);
        Arrays.sort(green);
        Arrays.sort(blue);
        r=red[4];
        g=green[4];
        b=blue[4];
        p = (a<<24) | (r<<16) | (g<<8) | b; 
    }
}
//-----------------------------------------------
class Filter_Mirror extends Filter{
    

    public Filter_Mirror(Image image){
        this.image = image;
        imageSetting();
        for(int x = 0; x < widthOfImage/2; x++) { 
            for(int y = 0; y < heightOfImage; y++) { 
                p = pixelReader.getArgb(x, y); 
                a = (p>>24)&0xff; 
                r = (p>>16)&0xff; 
                g = (p>>8)&0xff; 
                b = p&0xff; 
  
                 
  
                //set new RGB value to right side of image 
                p = (a<<24) | (r<<16) | (g<<8) | b; 
                pixelWriter.setArgb(x, y, p);   
                pixelWriter.setArgb(widthOfImage-1-x, y, p);       
                
                
            }
        }
    }
        
}
//-----------------------------------------------
class Filter_Negative extends Filter{


    public Filter_Negative(Image image){
        this.image = image;
        imageSetting();
        for(int x = 0; x < widthOfImage; x++) { 
            for(int y = 0; y < heightOfImage; y++) { 
                p = pixelReader.getArgb(x, y); 
                a = (p>>24)&0xff; 
                r = (p>>16)&0xff; 
                g = (p>>8)&0xff; 
                b = p&0xff; 
  
                //subtract RGB from 255 
                r = 255 - r; 
                g = 255 - g; 
                b = 255 - b; 
  
                //set new RGB value 
                p = (a<<24) | (r<<16) | (g<<8) | b; 
                pixelWriter.setArgb(x, y, p);       
                
                
            }
        }
        
        
        
        
    /*    
        //Reading the color of the image 
        for(int y = 0; y < heightOfImage; y++) { 
            for(int x = 0; x < widthOfImage; x++) { 
           //Retrieving the color of the pixel of the loaded image   
            Color color = pixelReader.getArgb(x, y); 
           //Setting the color to the writable image 
            pixelWriter.setColor(x, y, color.darker());              
            }
        }

    */
    }
}
//-----------------------------------------------
class Filter_Sharpen extends Filter{
    
    private class ValueOfARGB{
        public int a,r,g,b;
    }
    private ValueOfARGB [][]array;
    

    public Filter_Sharpen(Image image){
        this.image = image;
        imageSetting();
        
        array=new ValueOfARGB [widthOfImage][heightOfImage];
        for(int i=0;i<widthOfImage;i++)
            for(int j=0;j<heightOfImage;j++)    
                array[i][j]=new ValueOfARGB();
        
        for(int x = 0; x < widthOfImage; x++){//get ARGB
            for(int y = 0; y < heightOfImage; y++){
                p = pixelReader.getArgb(x, y); 
                a = (p>>24)&0xff; 
                r = (p>>16)&0xff; 
                g = (p>>8)&0xff; 
                b = p&0xff;
                
                array[x][y].a=a;
                array[x][y].r=r;
                array[x][y].g=g;
                array[x][y].b=b;
                
            }
        }

        for(int x = 1; x < widthOfImage-1; x++) { //get new ARGB
            for(int y = 1; y < heightOfImage-1; y++) { 
                Sharping(x,y);
                pixelWriter.setArgb(x, y, p);        
            }
        }
    }
 

    public void Sharping(int x,int y) {
        a =array[x][y].a;
        r=0;
        g=0;
        b=0;
        int [][]kernal={{0,-1,0},{-1,5,-1},{0,-1,0}};
        //System.out.println("&");
        /*for(int i=x-1;i<=x+1;i++){
            for(int j=y-1;j<=y+1;j++){
                r+=array[i][j].r*kernal[j-x+1][i-y+1];
                g+=array[i][j].g*kernal[j-x+1][i-y+1];
                b+=array[i][j].b*kernal[j-x+1][i-y+1];
            }
        }*/
        
        r += array[x-1][y-1].r*(0);  g += array[x-1][y-1].g*(0);  b += array[x-1][y-1].b*(0);
        r += array[x-1][y].r*(-1);   g += array[x-1][y].g*(-1);   b += array[x-1][y].b*(-1);
        r += array[x-1][y+1].r*(0);  g += array[x-1][y+1].g*(0);  b += array[x-1][y+1].b*(0);
        r += array[x][y-1].r*(-1);   g += array[x][y-1].g*(-1);   b += array[x][y-1].b*(-1);
        r += array[x][y].r*(5);      g += array[x][y].g*(5);      b += array[x][y].b*(5);
        r += array[x][y+1].r*(-1);   g += array[x][y+1].g*(-1);   b += array[x][y+1].b*(-1);
        r += array[x+1][y-1].r*(0);  g += array[x+1][y-1].g*(0);  b += array[x+1][y-1].b*(0);
        r += array[x+1][y].r*(-1);   g += array[x+1][y].g*(-1);   b += array[x+1][y].b*(-1);
        r += array[x+1][y+1].r*(0);  g += array[x+1][y+1].g*(0);  b += array[x+1][y+1].b*(-0);
        r=Math.min(Math.max(0,r),255);
        g=Math.min(Math.max(0,g),255);
        b=Math.min(Math.max(0,b),255);

        p = (a<<24) | (r<<16) | (g<<8) | b; 
    }
}
