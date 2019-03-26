package com.davide.verbatiam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class MainActivity extends Activity {


    private ImageView startI;
    private ImageView exitI;
    private ImageView exitS;
    private ImageView shopI;
    private ImageView layoutcoin;
    private TextView textcoin;
    private TextView textScore;

    //Layout dello ship
    private ConstraintLayout shipL;
    private ImageView exitShipLayout;
    private ImageView green;
    private ImageView red;
    private ImageView ultimate;
    private ImageView backButton;
    private ImageView buyButtonRed;
    private ImageView buyButtonUltimate;
    private ImageView selectionGreen;
    private ImageView selectionRed;
    private ImageView selectionUltimate;

    //Layout dello shop
    private ConstraintLayout shopL;
    private ImageView shipSection;
    private ImageView gunSection;
    private ImageView upgradeSection;

    //Layout dei guns
    private ConstraintLayout gunL;
    private ImageView exitG;
    private ImageView g1;
    private ImageView g2;
    private ImageView g3;
    private ImageView r1;
    private ImageView r2;
    private ImageView r3;
    private ImageView backButtonG;
    private ImageView buyButtonG2;
    private ImageView buyButtonG3;
    private ImageView buyButtonR2;
    private ImageView buyButtonR3;
    private ImageView selectionG1;
    private ImageView selectionG2;
    private ImageView selectionG3;
    private ImageView selectionR1;
    private ImageView selectionR2;
    private ImageView selectionR3;
    private TextView textView;

    //Layout degli upgrade
    private ConstraintLayout upgradeL;


    //Caricamento del gioco quando si preme su start
    private TextView caricamentoText;
    private ProgressBar caricamentoBar;

    //Handler usato per l'incemento della variabile "i" (fa partire il gioco)
    public Handler handlerCount = new Handler();
    public Runnable runnableCount;
    private int i = 0;

    //Classe importata per passare dati di riferimento
    private DrawableCostants drawableCostants = new DrawableCostants();
    private DatabaseHelper db;
    private Storage storage = new Storage();
    private boolean dbFlag = false;

    private Costants costants = new Costants();

    private boolean handlerFlag = true;

    private MediaPlayer introSong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        DisplayMetrics ds = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(ds);
        costants.SCREEN_WIDTH = ds.widthPixels;
        costants.SCREEN_HEIGHT = ds.heightPixels;

        db = new DatabaseHelper(this);

        introSong = MediaPlayer.create(this,R.raw.intro);
        introSong.setVolume(0.f,0.2f);
        introSong.start();
        introSong.setLooping(true);

        //3 tasti principali per interazione del menu
        startI = (ImageView) findViewById(R.id.start);
        exitI = (ImageView) findViewById(R.id.exit);
        shopI = (ImageView) findViewById(R.id.shop);
        layoutcoin = (ImageView) findViewById(R.id.layoutCoin);
        textcoin = (TextView) findViewById(R.id.textCoin);
        textScore = (TextView) findViewById(R.id.score);

        //Vari tipi di sezioni
        shipSection = (ImageView) findViewById(R.id.shipShop);
        gunSection = (ImageView) findViewById(R.id.gunShop);
        upgradeSection = (ImageView) findViewById(R.id.upgradeShop);

        //Layout collegato allo ship
        exitShipLayout = (ImageView) findViewById(R.id.shipExit);
        shipL = (ConstraintLayout) findViewById(R.id.shipLayout);
        green = (ImageView) findViewById(R.id.greenS);
        red = (ImageView) findViewById(R.id.redS);
        ultimate = (ImageView) findViewById(R.id.ultimateS);
        backButton = (ImageView) findViewById(R.id.backS);
        buyButtonRed = (ImageView) findViewById(R.id.buyR);
        buyButtonUltimate = (ImageView) findViewById(R.id.buyU);
        selectionGreen = (ImageView) findViewById(R.id.selectionG);
        selectionRed = (ImageView) findViewById(R.id.selectionR);
        selectionUltimate = (ImageView) findViewById(R.id.selectionU);

        //Layout collegato alle armi
        exitG = (ImageView) findViewById(R.id.gunExit);
        gunL = (ConstraintLayout) findViewById(R.id.gunLayout);
        g1 = (ImageView) findViewById(R.id.G1);
        g2 = (ImageView) findViewById(R.id.G2);
        g3 = (ImageView) findViewById(R.id.G3);
        r1 = (ImageView) findViewById(R.id.R1);
        r2 = (ImageView) findViewById(R.id.R2);
        r3 = (ImageView) findViewById(R.id.R3);
        textView = (TextView) findViewById(R.id.text2);
        backButtonG = (ImageView) findViewById(R.id.backG);
        buyButtonG2 = (ImageView) findViewById(R.id.buyG2);
        buyButtonG3 = (ImageView) findViewById(R.id.buyG3);
        buyButtonR2 = (ImageView) findViewById(R.id.buyR2);
        buyButtonR3 = (ImageView) findViewById(R.id.buyR3);
        selectionG1 = (ImageView) findViewById(R.id.selectionG1);
        selectionG2 = (ImageView) findViewById(R.id.selectionG2);
        selectionG3 = (ImageView) findViewById(R.id.selectionG3);
        selectionR1 = (ImageView) findViewById(R.id.selectionR1);
        selectionR2 = (ImageView) findViewById(R.id.selectionR2);
        selectionR3 = (ImageView) findViewById(R.id.selectionR3);

        //Layout collegato allo shop
        shopL = (ConstraintLayout) findViewById(R.id.shopLayout);
        exitS = (ImageView) findViewById(R.id.exitShop);

        //Caricamento del gioco quando si preme su start
        caricamentoBar = (ProgressBar) findViewById(R.id.caricamentoBar);
        caricamentoText = (TextView) findViewById(R.id.caricamentoText);

        Cursor res = db.selectData();
        if(res.getCount() != 0) {
            while (res.moveToNext()) {
                textcoin.setText((res.getString(1)));
                textScore.setText((res.getString(2)));
                storage.green = res.getInt(3);
                storage.red = res.getInt(4);
                storage.ultimate = res.getInt(5);
                storage.g1 = res.getInt(6);
                storage.g2= res.getInt(7);
                storage.g3= res.getInt(8);
                storage.r1= res.getInt(9);
                storage.r2 = res.getInt(10);
                storage.r3= res.getInt(11);
            }
        }

        System.out.println("green " + storage.green);
        System.out.println("red " + storage.red);
        System.out.println("ultimate " + storage.ultimate);
        System.out.println("g1 " + storage.g1);
        System.out.println("g2 " + storage.g2);
        System.out.println("g3 " + storage.g3);
        System.out.println("r1 " + storage.r1);
        System.out.println("r2 " + storage.r2);
        System.out.println("r3 " + storage.r3);

        if(storage.red == 0 && storage.ultimate == 0)
        {
            storage.green = 2;
            db.updateGreen(storage.green);
            if(storage.g2 == 0 && storage.g3 == 0)
            {
                storage.g1 = 2;
                db.updateG1(storage.g1);
            }
        }

        if(storage.green == 2)
        {
            if(storage.g1 == 2)
            {
                drawableCostants.setPos(0);
            }
            if(storage.g2 == 2)
            {
                drawableCostants.setPos(1);
            }
            if(storage.g3 == 2)
            {
                drawableCostants.setPos(2);
            }
        }
        if(storage.red == 2)
        {
            if(storage.r2 == 0 && storage.r3 == 0)
            {
                storage.r1 = 2;
                db.updateR1(storage.r1);
                drawableCostants.setPos(3);
            }
            if(storage.r2 == 2)
            {
                drawableCostants.setPos(4);
            }
            if(storage.r3 == 2)
            {
                drawableCostants.setPos(5);
            }
        }
        if(storage.ultimate == 2)
        {
            drawableCostants.setPos(6);
        }


        //Fa partire il gioco
        startI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caricamentoBar.setVisibility(View.VISIBLE);
                caricamentoText.setVisibility(View.VISIBLE);
                startI.setVisibility(View.INVISIBLE);
                exitI.setVisibility(View.INVISIBLE);
                shopI.setVisibility(View.INVISIBLE);
                handlerCount.post(runnableCount = new Runnable() {
                    @Override
                    public void run() {
                        if(handlerFlag == true)
                        {
                            System.out.println(caricamentoBar.getProgress());
                            if(caricamentoBar.getProgress() == 100 && count() >= 100)
                            {
                                startGame();
                                startI.setOnClickListener(null);
                                handlerCount.removeCallbacks(this);
                                handlerFlag = false;
                                i = 0;
                                caricamentoBar.setProgress(i);
                                introSong.stop();
                            }
                            else if(caricamentoBar.getProgress() < 100 && count() < 100)
                            {
                                count();
                                caricamentoBar.setProgress(count());
                                handlerFlag = true;
                            }
                            handlerCount.postDelayed(this, 500);
                        }

                    }
                });
            }
        });

        //Esce dall'applicazione
        exitI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        //Apre la sezione shop
        shopI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startI.setVisibility(View.INVISIBLE);
                exitI.setVisibility(View.INVISIBLE);
                shopI.setVisibility(View.INVISIBLE);
                shopL.setVisibility(View.VISIBLE);
            }
        });

        //Esce dalla sezione shop
        exitS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopL.setVisibility(View.INVISIBLE);
                startI.setVisibility(View.VISIBLE);
                exitI.setVisibility(View.VISIBLE);
                shopI.setVisibility(View.VISIBLE);
            }
        });

        //Premendo sull'immagine selezione astonavi,
        //si apre un secondo layout (Ship Layout)
        shipSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopL.setVisibility(View.INVISIBLE);
                shipL.setVisibility(View.VISIBLE);
            }
        });

        //Esce dal ship layout con il pulsante exit
        //rendendo visibile lo shop layout e
        //invisibile lo ship layout
        exitShipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopL.setVisibility(View.VISIBLE);
                shipL.setVisibility(View.INVISIBLE);
            }
        });

        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green.setVisibility(View.VISIBLE);
                red.setVisibility(View.INVISIBLE);
                ultimate.setVisibility(View.INVISIBLE);
                backButton.setVisibility(View.VISIBLE);
                selectionGreen.setVisibility(View.VISIBLE);
            }
        });

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green.setVisibility(View.INVISIBLE);
                red.setVisibility(View.VISIBLE);
                ultimate.setVisibility(View.INVISIBLE);
                backButton.setVisibility(View.VISIBLE);

                if(storage.red >= 1)
                {
                    buyButtonRed.setVisibility(View.INVISIBLE);
                    selectionRed.setVisibility(View.VISIBLE);
                }
                else
                {
                    buyButtonRed.setVisibility(View.VISIBLE);
                    selectionRed.setVisibility(View.INVISIBLE);
                }

                //Va alla posizione della astronave verde
                red.setX(336);
                red.setY(300);
            }
        });

        ultimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green.setVisibility(View.INVISIBLE);
                red.setVisibility(View.INVISIBLE);
                ultimate.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);

                if(storage.ultimate >= 1)
                {
                    buyButtonUltimate.setVisibility(View.INVISIBLE);
                    selectionUltimate.setVisibility(View.VISIBLE);
                }
                else
                {
                    buyButtonUltimate.setVisibility(View.VISIBLE);
                    selectionUltimate.setVisibility(View.INVISIBLE);
                }


                //Va alla posizione della astronave verde
                ultimate.setX(336);
                ultimate.setY(290);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green.setVisibility(View.VISIBLE);
                red.setVisibility(View.VISIBLE);
                ultimate.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.INVISIBLE);
                selectionGreen.setVisibility(View.INVISIBLE);
                buyButtonUltimate.setVisibility(View.INVISIBLE);
                selectionUltimate.setVisibility(View.INVISIBLE);
                buyButtonRed.setVisibility(View.INVISIBLE);
                selectionRed.setVisibility(View.INVISIBLE);

                //Ritorna alla posizione originale
                green.setX(336);
                green.setY(306);

                //Ritorna alla posizione originale
                red.setX(339);
                red.setY(685);

                //Ritorna alla posizione originale
                ultimate.setX(339);
                ultimate.setY(1083);
            }
        });

        selectionGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaGreen();
            }
        });

        buyButtonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyRed();

                if(storage.red >= 1)
                {
                    buyButtonRed.setVisibility(View.INVISIBLE);
                    selectionRed.setVisibility(View.VISIBLE);
                }
                else
                {
                    buyButtonRed.setVisibility(View.VISIBLE);
                    selectionRed.setVisibility(View.INVISIBLE);
                }
            }
        });

        selectionRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaRed();
            }
        });

        buyButtonUltimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyUltimate();

                if(storage.ultimate >= 1)
                {
                    buyButtonUltimate.setVisibility(View.INVISIBLE);
                    selectionUltimate.setVisibility(View.VISIBLE);
                }
                else
                {
                    buyButtonUltimate.setVisibility(View.VISIBLE);
                    selectionUltimate.setVisibility(View.INVISIBLE);
                }
            }
        });

        selectionUltimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaUltimate();
            }
        });

        //Premendo sull'immagine selezione astonavi,
        //si apre un secondo layout (gun Layout)
        gunSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopL.setVisibility(View.INVISIBLE);
                gunL.setVisibility(View.VISIBLE);
                if(storage.green == 2)
                {
                    textView.setText("Green");
                    r1.setVisibility(View.INVISIBLE);
                    r2.setVisibility(View.INVISIBLE);
                    r3.setVisibility(View.INVISIBLE);
                    g1.setVisibility(View.VISIBLE);
                    g2.setVisibility(View.VISIBLE);
                    g3.setVisibility(View.VISIBLE);
                }
                else if(storage.red == 2)
                {
                    textView.setText("Red");
                    r1.setVisibility(View.VISIBLE);
                    r2.setVisibility(View.VISIBLE);
                    r3.setVisibility(View.VISIBLE);
                    g1.setVisibility(View.INVISIBLE);
                    g2.setVisibility(View.INVISIBLE);
                    g3.setVisibility(View.INVISIBLE);
                }
            }
        });

        //Esce dal ship layout con il pulsante exit
        //rendendo visibile lo shop layout e
        //invisibile gun layout
        exitG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopL.setVisibility(View.VISIBLE);
                gunL.setVisibility(View.INVISIBLE);
                if(storage.green == 2)
                {
                    r1.setVisibility(View.INVISIBLE);
                    r2.setVisibility(View.INVISIBLE);
                    r3.setVisibility(View.INVISIBLE);
                    g1.setVisibility(View.VISIBLE);
                    g2.setVisibility(View.VISIBLE);
                    g3.setVisibility(View.VISIBLE);
                    backButtonG.setVisibility(View.INVISIBLE);
                    buyButtonG3.setVisibility(View.INVISIBLE);
                    buyButtonG2.setVisibility(View.INVISIBLE);

                    //Ritorna alla posizione originale
                    g1.setX(336);
                    g1.setY(306);

                    //Ritorna alla posizione originale
                    g2.setX(339);
                    g2.setY(685);

                    //Ritorna alla posizione originale
                    g3.setX(336);
                    g3.setY(1155);
                }
                else if(storage.red == 2)
                {
                    r1.setVisibility(View.VISIBLE);
                    r2.setVisibility(View.VISIBLE);
                    r3.setVisibility(View.VISIBLE);
                    g1.setVisibility(View.INVISIBLE);
                    g2.setVisibility(View.INVISIBLE);
                    g3.setVisibility(View.INVISIBLE);
                    backButtonG.setVisibility(View.INVISIBLE);
                    buyButtonR3.setVisibility(View.INVISIBLE);
                    buyButtonR2.setVisibility(View.INVISIBLE);

                    //Ritorna alla posizione originale
                    r1.setX(336);
                    r1.setY(306);

                    //Ritorna alla posizione originale
                    r2.setX(339);
                    r2.setY(685);

                    //Ritorna alla posizione originale
                    r3.setX(336);
                    r3.setY(1155);
                }
            }
        });

        g1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                g1.setVisibility(View.VISIBLE);
                g2.setVisibility(View.INVISIBLE);
                g3.setVisibility(View.INVISIBLE);
                backButtonG.setVisibility(View.VISIBLE);

                if(storage.g1 >= 1)
                {
                    selectionG1.setVisibility(View.VISIBLE);
                }
                else
                {
                    selectionG1.setVisibility(View.INVISIBLE);
                }
            }
        });

        g2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                g1.setVisibility(View.INVISIBLE);
                g2.setVisibility(View.VISIBLE);
                g3.setVisibility(View.INVISIBLE);
                backButtonG.setVisibility(View.VISIBLE);

                //Va alla posizione della astronave verde
                g2.setX(336);
                g2.setY(300);

                if(storage.g2 >= 1)
                {
                    buyButtonG2.setVisibility(View.INVISIBLE);
                    selectionG2.setVisibility(View.VISIBLE);
                }
                else
                {
                    buyButtonG2.setVisibility(View.VISIBLE);
                    selectionG2.setVisibility(View.INVISIBLE);
                }
            }
        });

        g3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                g1.setVisibility(View.INVISIBLE);
                g2.setVisibility(View.INVISIBLE);
                g3.setVisibility(View.VISIBLE);
                backButtonG.setVisibility(View.VISIBLE);
                buyButtonG3.setVisibility(View.VISIBLE);

                //Va alla posizione della astronave verde
                g3.setX(336);
                g3.setY(306);

                if(storage.g3 >= 1)
                {
                    buyButtonG3.setVisibility(View.INVISIBLE);
                    selectionG3.setVisibility(View.VISIBLE);
                }
                else
                {
                    buyButtonG3.setVisibility(View.VISIBLE);
                    selectionG3.setVisibility(View.INVISIBLE);
                }

            }
        });

        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r1.setVisibility(View.VISIBLE);
                r2.setVisibility(View.INVISIBLE);
                r3.setVisibility(View.INVISIBLE);
                backButtonG.setVisibility(View.VISIBLE);

                if(storage.r1 >= 1)
                {
                    selectionR1.setVisibility(View.VISIBLE);
                }
                else
                {
                    selectionR1.setVisibility(View.INVISIBLE);
                }
            }
        });

        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r1.setVisibility(View.INVISIBLE);
                r2.setVisibility(View.VISIBLE);
                r3.setVisibility(View.INVISIBLE);
                backButtonG.setVisibility(View.VISIBLE);
                buyButtonG2.setVisibility(View.VISIBLE);

                //Va alla posizione della astronave verde
                r2.setX(336);
                r2.setY(300);

                if(storage.r2 >= 1)
                {
                    buyButtonR2.setVisibility(View.INVISIBLE);
                    selectionR2.setVisibility(View.VISIBLE);
                }
                else
                {
                    buyButtonR2.setVisibility(View.VISIBLE);
                    selectionR2.setVisibility(View.INVISIBLE);
                }
            }
        });

        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r1.setVisibility(View.INVISIBLE);
                r2.setVisibility(View.INVISIBLE);
                r3.setVisibility(View.VISIBLE);
                backButtonG.setVisibility(View.VISIBLE);
                buyButtonG3.setVisibility(View.VISIBLE);

                //Va alla posizione della astronave verde
                r3.setX(336);
                r3.setY(306);

                if(storage.r3 >= 1)
                {
                    buyButtonR3.setVisibility(View.INVISIBLE);
                    selectionR3.setVisibility(View.VISIBLE);
                }
                else
                {
                    buyButtonR3.setVisibility(View.VISIBLE);
                    selectionR3.setVisibility(View.INVISIBLE);
                }

            }
        });

        backButtonG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backButtonG.setVisibility(View.INVISIBLE);
                buyButtonG3.setVisibility(View.INVISIBLE);
                buyButtonG2.setVisibility(View.INVISIBLE);
                buyButtonR3.setVisibility(View.INVISIBLE);
                buyButtonR2.setVisibility(View.INVISIBLE);
                selectionG1.setVisibility(View.INVISIBLE);
                selectionG2.setVisibility(View.INVISIBLE);
                selectionG3.setVisibility(View.INVISIBLE);
                selectionR1.setVisibility(View.INVISIBLE);
                selectionR2.setVisibility(View.INVISIBLE);
                selectionR3.setVisibility(View.INVISIBLE);

                if(storage.green == 2)
                {
                    r1.setVisibility(View.INVISIBLE);
                    r2.setVisibility(View.INVISIBLE);
                    r3.setVisibility(View.INVISIBLE);
                    g1.setVisibility(View.VISIBLE);
                    g2.setVisibility(View.VISIBLE);
                    g3.setVisibility(View.VISIBLE);

                    //Ritorna alla posizione originale
                    g1.setX(336);
                    g1.setY(306);

                    //Ritorna alla posizione originale
                    g2.setX(339);
                    g2.setY(685);

                    //Ritorna alla posizione originale
                    g3.setX(336);
                    g3.setY(1155);
                }
                else if(storage.red == 2)
                {
                    r1.setVisibility(View.VISIBLE);
                    r2.setVisibility(View.VISIBLE);
                    r3.setVisibility(View.VISIBLE);
                    g1.setVisibility(View.INVISIBLE);
                    g2.setVisibility(View.INVISIBLE);
                    g3.setVisibility(View.INVISIBLE);

                    //Ritorna alla posizione originale
                    r1.setX(336);
                    r1.setY(306);

                    //Ritorna alla posizione originale
                    r2.setX(339);
                    r2.setY(685);

                    //Ritorna alla posizione originale
                    r3.setX(336);
                    r3.setY(1155);
                }

            }
        });

        buyButtonG2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyG2();
            }
        });

        buyButtonG3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyG3();
            }
        });

        buyButtonR2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyR2();
            }
        });

        buyButtonR3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyR3();
            }
        });

        selectionG1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaG1();
            }
        });

        selectionG2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaG2();
            }
        });

        selectionG3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaG3();
            }
        });

        selectionR1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaR1();
            }
        });

        selectionR2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaR2();
            }
        });

        selectionR3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaR3();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onUserLeaveHint()
    {
        super.onUserLeaveHint();
    }

    public void buyRed()
    {
        long redCoin = 5000L;
        long currentCoin = 0L;

        Cursor res = db.selectData();

        while (res.moveToNext()) {
            currentCoin = res.getLong(1);
        }

        if(currentCoin >= redCoin && storage.red == 0)
        {
            currentCoin = currentCoin - redCoin;
            storage.coinStorageF = storage.coinStorageF - redCoin;

            db.updateData(currentCoin);
            textcoin.setText(currentCoin+"");

            storage.r1 = 1;
            storage.red = 1;
            db.updateRed(storage.red);
            db.updateR1(storage.r1);
        }
        else
        {
            System.out.println("Mi dispiace ma non puoi acquistare questa nave, perchè non hai abbastanza soldi. \n L'astronave costa 5000 coin mentre tu ne possiedi " + currentCoin + "\nOppure e' perche' hai gia' acquistato questa astronave");
        }
    }

    public void buyUltimate()
    {
        long ultimateCoin = 20000L;
        long currentCoin = 0L;

        Cursor res = db.selectData();

        while (res.moveToNext()) {
            currentCoin = res.getLong(1);
        }

        if(currentCoin >= ultimateCoin && storage.ultimate == 0)
        {
            currentCoin = currentCoin - ultimateCoin;
            storage.coinStorageF = storage.coinStorageF - ultimateCoin;

            db.updateData(currentCoin);
            textcoin.setText(currentCoin+"");

            storage.ultimate = 1;
            db.updateUltimate(storage.ultimate);

        }
        else
        {
            System.out.println("Mi dispiace ma non puoi acquistare questa nave, perchè non hai abbastanza soldi. \n L'astronave costa 5000 coin mentre tu ne possiedi " + currentCoin + "\nOppure e' perche' hai gia' acquistato questa astronave");
        }
    }

    public void selezionaGreen()
    {
        if(storage.green == 1)
        {
            storage.green = 2;

            if(storage.red == 2 || storage.ultimate == 2)
            {
                storage.red = 1;
                db.updateRed(storage.red);

                storage.ultimate = 1;
                db.updateUltimate(storage.ultimate);
            }
            db.updateGreen(storage.green);
        }
        else
        {
            System.out.println("Messaggio di errore");
        }
    }

    public void selezionaRed()
    {
        if(storage.red == 1)
        {
            storage.red = 2;

            if(storage.green == 2 || storage.ultimate == 2)
            {
                storage.green = 1;
                db.updateGreen(storage.green);

                storage.ultimate = 1;
                db.updateUltimate(storage.ultimate);
            }

            db.updateRed(storage.red);
        }
        else
        {
            System.out.println("Messaggio di errore");
        }
    }

    public void selezionaUltimate()
    {
        if(storage.ultimate == 1)
        {
            storage.ultimate = 2;

            if(storage.green == 2 || storage.red == 2 || storage.g1 == 2 || storage.g2 == 2 || storage.g3 == 2 || storage.r1 == 2 || storage.r2 == 2 || storage.r3 == 2)
            {
                storage.green = 1;
                db.updateGreen(storage.green);

                storage.red = 1;
                db.updateRed(storage.red);

                storage.g1 = 1;
                db.updateG1(storage.g1);
                storage.g2 = 1;
                db.updateG2(storage.g2);
                storage.g3 = 1;
                db.updateG2(storage.g3);

                storage.r1 = 1;
                db.updateR1(storage.r1);
                storage.r2 = 1;
                db.updateR2(storage.r2);
                storage.r3 = 1;
                db.updateR3(storage.r3);
            }

            db.updateUltimate(storage.ultimate);
            drawableCostants.setPos(6);
        }
        else
        {
            System.out.println("Messaggio di errore");
        }
    }

    /*
        green = gratis
        red = 8000
        ultimate = 20000
        g1 = gratis
        g2 = 2000
        g3 = 4000
        r1 = gratis
        r2 = 10000
        r3 = 14000
        u2 = 20000
    * */

    public void selezionaG1()
    {
        if(storage.g1 == 1)
        {
            storage.g1 = 2;

            if(storage.ultimate == 2 || storage.red == 2 || storage.g2 == 2 || storage.g3 == 2 || storage.r1 == 2 || storage.r2 == 2 || storage.r3 == 2)
            {
                storage.red = 1;
                db.updateRed(storage.red);
                storage.ultimate = 1;
                db.updateUltimate(storage.ultimate);

                storage.g2 = 1;
                db.updateG2(storage.g2);
                storage.g3 = 1;
                db.updateG2(storage.g3);

                storage.r1 = 1;
                db.updateR1(storage.r1);
                storage.r2 = 1;
                db.updateR2(storage.r2);
                storage.r3 = 1;
                db.updateR3(storage.r3);
            }

            db.updateG1(storage.g1);
            drawableCostants.setPos(0);
        }
        else
        {
            System.out.println("Messaggio di errore");
        }
    }

    public void selezionaG2()
    {
        if(storage.g2 == 1)
        {
            storage.g2 = 2;

            if(storage.ultimate == 2 || storage.red == 2 || storage.g1 == 2 || storage.g3 == 2 || storage.r1 == 2 || storage.r2 == 2 || storage.r3 == 2)
            {
                storage.red = 1;
                db.updateRed(storage.red);
                storage.ultimate = 1;
                db.updateUltimate(storage.ultimate);

                storage.g1 = 1;
                db.updateG1(storage.g1);
                storage.g3 = 1;
                db.updateG3(storage.g3);

                storage.r1 = 1;
                db.updateR1(storage.r1);
                storage.r2 = 1;
                db.updateR2(storage.r2);
                storage.r3 = 1;
                db.updateR3(storage.r3);
            }

            db.updateG2(storage.g2);
            drawableCostants.setPos(1);
        }
        else
        {
            System.out.println("Messaggio di errore");
        }
    }

    public void selezionaG3()
    {
        if(storage.g3 == 1)
        {
            storage.g3 = 2;

            if(storage.ultimate == 2 || storage.red == 2 || storage.g2 == 2 || storage.g1 == 2 || storage.r1 == 2 || storage.r2 == 2 || storage.r3 == 2)
            {
                storage.red = 1;
                db.updateRed(storage.red);
                storage.ultimate = 1;
                db.updateUltimate(storage.ultimate);

                storage.g2 = 1;
                db.updateG2(storage.g2);
                storage.g1 = 1;
                db.updateG1(storage.g1);

                storage.r1 = 1;
                db.updateR1(storage.r1);
                storage.r2 = 1;
                db.updateR2(storage.r2);
                storage.r3 = 1;
                db.updateR3(storage.r3);
            }

            db.updateG3(storage.g3);
            drawableCostants.setPos(2);
        }
        else
        {
            System.out.println("Messaggio di errore");
        }
    }

    public void selezionaR1()
    {
        if(storage.r1 == 1)
        {
            storage.r1 = 2;

            if(storage.green >= 1 || storage.red >= 1 || storage.g2 >= 1 || storage.g1 >= 1 || storage.g3 >= 1 || storage.r2 >= 1 || storage.r3 >= 1)
            {
                storage.green = 1;
                db.updateGreen(storage.green);
                storage.ultimate = 1;
                db.updateUltimate(storage.ultimate);

                storage.g2 = 1;
                db.updateG2(storage.g2);
                storage.g1 = 1;
                db.updateG1(storage.g1);
                storage.g3 = 1;
                db.updateG3(storage.g3);

                storage.r2 = 1;
                db.updateR2(storage.r2);
                storage.r3 = 1;
                db.updateR3(storage.r3);
            }

            db.updateR1(storage.r1);
            drawableCostants.setPos(3);
        }
        else
        {
            System.out.println("Messaggio di errore");
        }
    }

    public void selezionaR2()
    {
        if(storage.r2 == 1)
        {
            storage.r2 = 2;

            if(storage.green >= 1 || storage.red >= 1 || storage.g2 >= 1 || storage.g1 >= 1 || storage.g3 >= 1 || storage.r1 >= 1 || storage.r3 >= 1 )
            {
                storage.green = 1;
                db.updateGreen(storage.green);
                storage.ultimate = 1;
                db.updateUltimate(storage.ultimate);

                storage.g2 = 1;
                db.updateG2(storage.g2);

                storage.g1 = 1;
                db.updateG1(storage.g1);

                storage.g3 = 1;
                db.updateG3(storage.g3);

                storage.r1 = 1;
                db.updateR1(storage.r1);

                storage.r3 = 1;
                db.updateR3(storage.r3);
            }

            db.updateR2(storage.r2);
            drawableCostants.setPos(4);
        }
        else
        {
            System.out.println("Messaggio di errore");
        }
    }

    public void selezionaR3()
    {
        if(storage.r3 == 1)
        {
            storage.r3 = 2;

            if(storage.green == 2 || storage.red == 2 || storage.g2 == 2 || storage.g1 == 2 || storage.g3 == 2 || storage.r2 == 2 || storage.r1 == 2)
            {
                storage.green = 1;
                db.updateGreen(storage.green);
                storage.ultimate = 1;
                db.updateUltimate(storage.ultimate);

                storage.g2 = 1;
                db.updateG2(storage.g2);

                storage.g1 = 1;
                db.updateG1(storage.g1);

                storage.g3 = 1;
                db.updateG3(storage.g3);

                storage.r2 = 1;
                db.updateR2(storage.r2);

                storage.r1 = 1;
                db.updateR1(storage.r1);
            }

            db.updateR3(storage.r3);
            drawableCostants.setPos(5);
        }
        else
        {
            System.out.println("Messaggio di errore");
        }
    }

    public void buyG2()
    {
        long g2 = 2000L;
        long currentCoin = 0L;

        Cursor res = db.selectData();

        while (res.moveToNext()) {
            currentCoin = res.getLong(1);
        }

        if(currentCoin >= g2 && storage.g2 == 0)
        {
            currentCoin = currentCoin - g2;
            storage.coinStorageF = storage.coinStorageF - g2;

            db.updateData(currentCoin);
            textcoin.setText(currentCoin+"");

            storage.g2 = 1;
            db.updateG2(storage.g2);
            System.out.println("Acquistato");
        }
        else
        {
            System.out.println("Mi dispiace ma non puoi acquistare questa arma perchè non hai abbastanza soldi. \n L'arma costa 5000 coin mentre tu ne possiedi " + currentCoin + "\nOppure e' perche' hai gia' acquistato questa arma");
        }
    }

    public void buyG3()
    {
        long g3 = 4000L;
        long currentCoin = 0L;

        Cursor res = db.selectData();

        while (res.moveToNext()) {
            currentCoin = res.getLong(1);
        }

        if(currentCoin >= g3 && storage.g3 == 0)
        {
            currentCoin = currentCoin - g3;
            storage.coinStorageF = storage.coinStorageF - g3;

            db.updateData(currentCoin);
            textcoin.setText(currentCoin+"");

            storage.g3 = 1;
            db.updateG3(storage.g3);
            System.out.println("Acquistato");
        }
        else
        {
            System.out.println("Mi dispiace ma non puoi acquistare questa arma perchè non hai abbastanza soldi. \n L'arma costa 5000 coin mentre tu ne possiedi " + currentCoin + "\nOppure e' perche' hai gia' acquistato questa arma");
        }
    }

    public void buyR2()
    {
        long r2 = 10000L;
        long currentCoin = 0L;

        Cursor res = db.selectData();

        while (res.moveToNext()) {
            currentCoin = res.getLong(1);
        }

        if(currentCoin >= r2 && storage.r2 == 0)
        {
            currentCoin = currentCoin - r2;
            storage.coinStorageF = storage.coinStorageF - r2;

            db.updateData(currentCoin);
            textcoin.setText(currentCoin+"");

            storage.r2 = 1;
            db.updateR2(storage.r2);
            System.out.println("Acquistato");
        }
        else
        {
            System.out.println("Mi dispiace ma non puoi acquistare questa arma perchè non hai abbastanza soldi. \n L'arma costa 5000 coin mentre tu ne possiedi " + currentCoin + "\nOppure e' perche' hai gia' acquistato questa arma");
        }
    }

    public void buyR3()
    {
        long r3 = 14000L;
        long currentCoin = 0L;

        Cursor res = db.selectData();

        while (res.moveToNext()) {
            currentCoin = res.getLong(1);
        }

        if(currentCoin >= r3 && storage.r3 == 0)
        {
            currentCoin = currentCoin - r3;
            storage.coinStorageF = storage.coinStorageF - r3;

            db.updateData(currentCoin);
            textcoin.setText(currentCoin+"");

            storage.r3 = 1;
            db.updateR3(storage.r3);
            System.out.println("Acquistato");
        }
        else
        {
            System.out.println("Mi dispiace ma non puoi acquistare questa arma perchè non hai abbastanza soldi. \n L'arma costa 5000 coin mentre tu ne possiedi " + currentCoin + "\nOppure e' perche' hai gia' acquistato questa arma");
        }
    }

    //Metodo usato per caricare la seconda activity
    public void startGame()
    {
        Cursor res = db.selectData();
        while (res.moveToNext()) {
            storage.coinStorageF = res.getLong(1);
        }
        Intent intent = new Intent(MainActivity.this, GamePanel.class);
        startActivity(intent);
    }

    //Metodo usato per incrementare la progress bar
    public int count()
    {
        if(i < 50)
        {
            i = i + 5;
        }
        else if(i >=50)
        {
            i = i + 2;
        }
        return i;
    }

}

