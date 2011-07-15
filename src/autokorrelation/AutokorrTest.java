package autokorrelation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.cloudgarden.resource.SWTResourceManager;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class AutokorrTest extends org.eclipse.swt.widgets.Composite {

	
	private static final int MAXDIFF = 1050;
	
	private Menu menu1;
	private Composite cmpPics;
	private ScrolledComposite scrolledComposite1;
	private MenuItem openFileMenuItem;
	private Menu fileMenu;
	private MenuItem fileMenuItem;
	protected int mouseStartY = -1;
	
//	private String f1 = "./testpics/1.png";
//	private String f2 = "./testpics/2.png";

	private String f1 = "./testpics/r248.bmp";
	private String f2 = "./testpics/r249.bmp";

	private int diff = 1;
	private Image i1;
	private Image i2;
	int[] results = new int[MAXDIFF];
	ImageData i1Data;// = new ImageData(f1);
	ImageData i2Data;// = new ImageData(f2);
	
	PaintListener paintPics = new PaintListener(){

		public void paintControl(PaintEvent arg0) {
			GC gc = arg0.gc;
			int i2Y = i1.getBounds().height-diff;
			
			gc.drawImage(i1, 0, 0);
			gc.drawImage(i2, i1.getBounds().width, i2Y);
			
			/* Draw result diagram */
			gc.setForeground(SWTResourceManager.getColor(255, 0, 0));
			
			for (int y=0;y<results.length;y++){
				gc.drawLine(
						i1.getBounds().width *2, 
						i1.getBounds().height-y, 
						i1.getBounds().width *2 + results[y]/200, 
						i1.getBounds().height-y);
				
			}
			
			gc.setForeground(SWTResourceManager.getColor(0, 0, 0));
			
			drawAmplitudes(gc);
			drawDiff(gc);
		
			gc.dispose();
			
		}
		
	};

	{
		//Register as a resource user - SWTResourceManager will
		//handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	public AutokorrTest(Composite parent, int style) {
		super(parent, style);
		i1 = new Image(null,f1);
		i2 = new Image(null,f2);
		i1Data = new ImageData(f1);
		i2Data = new ImageData(f2);
		
		initGUI();
	}
	
	protected void drawDiff(GC gc) {
		gc.setForeground(SWTResourceManager.getColor(0, 100, 0));
		gc.drawLine(0, i1Data.height-diff, 1280, i1Data.height-diff);
		gc.setForeground(SWTResourceManager.getColor(0, 0, 0));
		gc.drawText(String.valueOf(diff), 100, i1Data.height-diff);
			
	}

	protected void drawAmplitudes(GC gc) {
		gc.setAlpha(100);
		int[] i1Amplitudes = Autokorrelate.getAmplitudes(i1Data,0,i1Data.height);
		for (int y=0;y<i1Amplitudes.length;y++){
			gc.drawLine(i1Data.width, y, i1Data.width+i1Amplitudes[y], y);
		}
		gc.setAlpha(255);
	}

	private int findMaxKorrelation(int start, int end){
		int maxD = 1;
		int maxV = 0;
		for (int d=start;d<end;d++){
			int aK = Autokorrelate.calcAutokorrDiff(d, i1Data, i2Data);
			results[d] = aK;
			if (aK > maxV){
				maxV = aK;
				maxD = d;
			} 
		}
		return  maxD;
	}
	
	private void manual(int d){
		results[d] = Autokorrelate.calcAutokorrDiff(d, i1Data, i2Data);
	}
	
	/**
	* Initializes the GUI.
	*/
	private void initGUI() {
		try {
			this.setSize(621, 315);
			this.setBackground(SWTResourceManager.getColor(192, 192, 192));
			FillLayout thisLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
			this.setLayout(thisLayout);
			{
				scrolledComposite1 = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
				FillLayout scrolledComposite1Layout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
				scrolledComposite1.setLayout(scrolledComposite1Layout);
				{
					cmpPics = new Composite(scrolledComposite1, SWT.NONE);
					scrolledComposite1.setContent(cmpPics);
					GridLayout cmpPicsLayout = new GridLayout();
					cmpPicsLayout.makeColumnsEqualWidth = true;
					cmpPics.setLayout(cmpPicsLayout);
					cmpPics.setBounds(0, 0, 1280, 2000);
					cmpPics.addPaintListener(paintPics);
					cmpPics.addMouseListener(new MouseListener(){

						public void mouseDoubleClick(MouseEvent arg0) {
							// TODO Auto-generated method stub
							
						}

						public void mouseDown(MouseEvent arg0) {
							mouseStartY = arg0.y;
							
						}

						public void mouseUp(MouseEvent arg0) {
							mouseStartY = -1;							
						}
						
					});
					cmpPics.addMouseMoveListener(new MouseMoveListener(){

						public void mouseMove(MouseEvent arg0) {
							if (mouseStartY>=0){
								diff = mouseStartY-arg0.y;
								diff = Math.max(0, diff);
								manual(diff);
								redraw();
							}
							
						}
						
					});
					cmpPics.addKeyListener(new KeyAdapter(){
						
						public void keyReleased(KeyEvent arg0) {
							switch (arg0.character) {
							case 'n':
								manual(diff++);
								break;
							case 'm':
								manual(diff--);
								break;
							case 'a':
								diff = findMaxKorrelation(diff, diff+100);
								redraw();	
							default:
								break;
							}
							redraw();
							System.out.println("MAN "+diff+" "+results[diff]);
							
						}
						
					});
				}
			}
			{
				menu1 = new Menu(getShell(), SWT.BAR);
				getShell().setMenuBar(menu1);
				{
					fileMenuItem = new MenuItem(menu1, SWT.CASCADE);
					fileMenuItem.setText("File");
					{
						fileMenu = new Menu(fileMenuItem);
						{
							openFileMenuItem = new MenuItem(fileMenu, SWT.CASCADE);
							openFileMenuItem.setText("Open");
						}
						
						fileMenuItem.setMenu(fileMenu);
					}
				}
			}
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		AutokorrTest inst = new AutokorrTest(shell, SWT.NULL);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
