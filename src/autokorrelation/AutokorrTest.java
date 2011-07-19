package autokorrelation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import playground.ImageDataSource;
import playground.ImageWaveWriterPlayer;

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

	ImageWaveWriterPlayer player = new ImageWaveWriterPlayer(new ImageDataSource());
	
	private static final int MAXDIFF = 1050;
	
	private Menu menu1;
	private Composite cmpPics;
	private Button btnFwd;
	private Button btnBack;
	private Composite composite1;
	private ScrolledComposite scrolledComposite1;
	private MenuItem openFileMenuItem;
	private Menu fileMenu;
	private MenuItem fileMenuItem;
	protected int mouseStartY = -1;
	private int region  = 100;
	private Spinner spinner1;
	private Label lblPic;
	private Button btnPlay;
	private MouseBehaviour mouseBehaviour;
	
	private String f1 = "./testpics/g2.bmp";
	private String f2 = "./testpics/g1.bmp";
	int index = 5;

	private int diff = 1;
	private Image i1;
	private Image i2;
	private int[] results = new int[MAXDIFF];
	private ImageData i1Data;// = new ImageData(f1);
	private ImageData i2Data;// = new ImageData(f2);
	protected boolean mouseDown;
	private List<Integer> klickedYs = new ArrayList<Integer>();
	protected int mouseY;
	private Autokorrelator autokorrelator = new Autokorrelator();
	
	MouseBehaviour korrBehaviour = new  MouseBehaviour(){

		public void mouseDoubleClick(MouseEvent arg0) {
		}

		public void mouseDown(MouseEvent arg0) {
			mouseStartY = diff+arg0.y;
		}

		public void mouseUp(MouseEvent arg0) {
			mouseStartY = -1;			
		}

		public void mouseMove(MouseEvent arg0) {
			if (mouseStartY>=0){
				diff = mouseStartY-arg0.y;
				diff = Math.max(0, diff);
				results[diff] = autokorrelator.calcAutokorrDiff(diff, i1Data, i2Data);
				redraw();
			}
		}
		
	};

	
	MouseBehaviour measureBehaviour = new  MouseBehaviour(){
		
		public void mouseDoubleClick(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public void mouseDown(MouseEvent arg0) {
			klickedYs.add(arg0.y);
			if (klickedYs.size()>=2){
				mouseBehaviour = korrBehaviour;
			}
			mouseDown = true;
			redraw();
		}

		public void mouseUp(MouseEvent arg0) {
			mouseDown = false;
			
		}

		public void mouseMove(MouseEvent arg0) {
			mouseY = arg0.y;
			redraw();
			
		}
		
	};
	
	
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
						i1.getBounds().width *2 + results[y], 
						i1.getBounds().height-y);
				
			}
			
			gc.setForeground(SWTResourceManager.getColor(0, 0, 0));
			
			drawAmplitudes(gc);
			drawDiff(gc);
			drawMeas(gc);
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
		loadPics();
		
		initGUI();
		
		player.addImageListener(new ImageListener(){
			public void imageChanged(final String path) {
				getDisplay().syncExec(new Runnable(){

					public void run() {
						if (lblPic.getImage()!=null){
							lblPic.getImage().dispose();
						}
						lblPic.setImage(new Image(null,path));
					}
					
				});
			}
		});
	}
	
	private void loadPics() {
		i1 = new Image(null,f1);
		i2 = new Image(null,f2);
		i1Data = new ImageData(f1);
		i2Data = new ImageData(f2);
		
	}

	protected void drawDiff(GC gc) {
		gc.setForeground(SWTResourceManager.getColor(0, 100, 0));
		gc.drawLine(0, i1Data.height-diff, 1280, i1Data.height-diff);
		gc.drawLine(0, i1Data.height-diff-region, 1280, i1Data.height-diff-region);
		gc.setForeground(SWTResourceManager.getColor(0, 0, 0));
		gc.drawText(String.valueOf(diff), 100, i1Data.height-diff);
	}
	

	protected void drawMeas(GC gc) {
		gc.setForeground(SWTResourceManager.getColor(0, 100, 100));
		int width = 500;
		switch (klickedYs.size()) {
		case 3:
		case 2:	
			gc.setForeground(SWTResourceManager.getColor(200, 100, 100));
			gc.drawLine(0, klickedYs.get(1), width, klickedYs.get(1));
			gc.drawLine(0, klickedYs.get(0), width, klickedYs.get(0));
			gc.drawLine(0, klickedYs.get(0)+(klickedYs.get(0)-klickedYs.get(1)), width, klickedYs.get(0)+(klickedYs.get(0)-klickedYs.get(1)));
			break;
		case 1:
			gc.setForeground(SWTResourceManager.getColor(200, 100, 100));
			gc.drawLine(0, klickedYs.get(0), width, klickedYs.get(0));
			gc.drawLine(0, mouseY, width, mouseY);
			gc.drawLine(0, klickedYs.get(0)+(klickedYs.get(0)-mouseY), width, klickedYs.get(0)+(klickedYs.get(0)-mouseY));
			
		case 0:
			gc.drawLine(0, mouseY, 100, mouseY);
			
		default:
			break;
		}
		gc.setForeground(SWTResourceManager.getColor(0, 0, 0));
			
	}

	protected void drawAmplitudes(GC gc) {
		gc.setAlpha(100);
		int[] i1Amplitudes = autokorrelator.getAmplitudes(i1Data,0,i1Data.height);
		for (int y=0;y<i1Amplitudes.length;y++){
			gc.drawLine(i1Data.width, y, i1Data.width+i1Amplitudes[y], y);
		}
		gc.setAlpha(255);
	}
	
	
	/**
	* Initializes the GUI.
	*/
	private void initGUI() {
		
		mouseBehaviour = korrBehaviour;
		
		try {
			this.setSize(715, 411);
			this.setBackground(SWTResourceManager.getColor(192, 192, 192));
			FormLayout thisLayout = new FormLayout();
			this.setLayout(thisLayout);
			{
				FormData lblPicLData = new FormData();
				lblPicLData.width = 270;
				lblPicLData.height = 343;
				lblPicLData.left =  new FormAttachment(614, 1000, 0);
				lblPicLData.right =  new FormAttachment(992, 1000, 0);
				lblPicLData.top =  new FormAttachment(152, 1000, 0);
				lblPicLData.bottom =  new FormAttachment(986, 1000, 0);
				lblPic = new Label(this, SWT.NONE);
				lblPic.setLayoutData(lblPicLData);
			}
			{
				composite1 = new Composite(this, SWT.NONE);
				RowLayout composite1Layout = new RowLayout(org.eclipse.swt.SWT.HORIZONTAL);
				composite1.setLayout(composite1Layout);
				FormData composite1LData = new FormData();
				composite1LData.width = 715;
				composite1LData.height = 54;
				composite1LData.left =  new FormAttachment(0, 1000, 0);
				composite1LData.right =  new FormAttachment(1000, 1000, 0);
				composite1LData.top =  new FormAttachment(0, 1000, 0);
				composite1.setLayoutData(composite1LData);
				{
					btnBack = new Button(composite1, SWT.PUSH | SWT.CENTER);
					btnBack.setText("<");
				}
				{
					btnFwd = new Button(composite1, SWT.PUSH | SWT.CENTER);
					btnFwd.setText(">");
					btnFwd.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent evt) {
							loadNextPics();
						}
					});
				}
				{
					btnPlay = new Button(composite1, SWT.PUSH | SWT.CENTER);
					btnPlay.setText("Play");
					btnPlay.addSelectionListener(new SelectionListener(){

						public void widgetDefaultSelected(SelectionEvent arg0) {
							// TODO Auto-generated method stub
							
						}

						public void widgetSelected(SelectionEvent arg0) {
							startPlayer();
							
						}
						
					});
				}
				{
					RowData spinner1LData = new RowData();
					spinner1LData.width = 56;
					spinner1LData.height = 13;
					spinner1 = new Spinner(composite1, SWT.NONE);
					spinner1.setLayoutData(spinner1LData);
					spinner1.addSelectionListener(new SelectionListener(){

						public void widgetDefaultSelected(SelectionEvent arg0) {
							// TODO Auto-generated method stub
							
						}

						public void widgetSelected(SelectionEvent arg0) {
							player.setNoiseThreshold(spinner1.getSelection());
							
						}
						
					});
				}
			}
			{
				scrolledComposite1 = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
				FillLayout scrolledComposite1Layout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
				scrolledComposite1.setLayout(scrolledComposite1Layout);
				FormData scrolledComposite1LData = new FormData();
				scrolledComposite1LData.width = 413;
				scrolledComposite1LData.height = 333;
				scrolledComposite1LData.right =  new FormAttachment(1000, 1000, -283);
				scrolledComposite1LData.left =  new FormAttachment(0, 1000, 2);
				scrolledComposite1LData.bottom =  new FormAttachment(1000, 1000, 0);
				scrolledComposite1LData.top =  new FormAttachment(0, 1000, 61);
				scrolledComposite1.setLayoutData(scrolledComposite1LData);
				{
					cmpPics = new Composite(scrolledComposite1, SWT.NONE);
					scrolledComposite1.setContent(cmpPics);
					GridLayout cmpPicsLayout = new GridLayout();
					cmpPicsLayout.makeColumnsEqualWidth = true;
					cmpPics.setLayout(cmpPicsLayout);
					cmpPics.setBounds(0, 0, 2000, 2000);
					cmpPics.addPaintListener(paintPics);
					cmpPics.addMouseListener(new MouseAdapter(){

						public void mouseDown(MouseEvent arg0) {
							mouseBehaviour.mouseDown(arg0);
						}

						public void mouseUp(MouseEvent arg0) {
							mouseBehaviour.mouseUp(arg0);				
						}
						
					});
					cmpPics.addMouseMoveListener(new MouseMoveListener(){

						public void mouseMove(MouseEvent arg0) {
							
							mouseBehaviour.mouseMove(arg0);
						}
						
					});
					
					cmpPics.addKeyListener(new KeyAdapter(){
						
						public void keyReleased(KeyEvent arg0) {
							switch (arg0.character) {
							case 'n':
								autokorrelator.calcAutokorrDiff(diff++, i1Data, i2Data);
								break;
							case 'm':
								autokorrelator.calcAutokorrDiff(diff--, i1Data, i2Data);
								break;
							case 'a':
								diff = autokorrelator.findMaxKorrelation(diff, diff+region, i1Data, i2Data,results);
								redraw();	
								break;
							case 'p':
								mouseBehaviour = measureBehaviour;
								klickedYs.clear();
								break;
							case 'o':
								mouseBehaviour = korrBehaviour;
								break;
								
							default:
								break;
							}
							redraw();
							
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
	
	
	protected void startPlayer() {
		Runnable play = new Runnable(){

			public void run() {
				try {
					player.process();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread playThread = new Thread(play);
		playThread.start();
	}

	protected void loadNextPics() {
		 f1 = "/Users/andreasrettig/Desktop/tarot/"+String.format("TarotMechanic%05d.bmp", index);
		 index++;
		 f2 = "/Users/andreasrettig/Desktop/tarot/"+String.format("TarotMechanic%05d.bmp", index);
		 loadPics();
		 redraw();
		 results = new int[results.length];
		 diff = autokorrelator.findMaxKorrelation(0, 100, i1Data, i2Data, results);
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
