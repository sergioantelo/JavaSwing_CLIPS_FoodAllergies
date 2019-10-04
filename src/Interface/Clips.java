/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Pojos.RestaurantUser;
import Pojos.Allergen;
import Pojos.Restaurant;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.sf.clipsrules.jni.CLIPSException;
import net.sf.clipsrules.jni.CLIPSLoadException;
import net.sf.clipsrules.jni.Environment;
import net.sf.clipsrules.jni.FactAddressValue;

/**
 *
 * @author Pablo
 */
public final class Clips extends javax.swing.JFrame implements ListSelectionListener{

    Allergen allergen;
    Environment clips;
    Browser browser = new Browser();
    ArrayList<RestaurantUser> restUserOrdered = new ArrayList();
    ArrayList<Restaurant> restaurants = new ArrayList();
                
    JList<String> resName = new JList<String>();
    DefaultListModel<String> modelList = new DefaultListModel<String>();
    int selectedIndex;
    
    /**
     * Creates new form ClipsFrame
     * @param allergen
     * @throws net.sf.clipsrules.jni.CLIPSLoadException
     */
    
    public Clips(Allergen allergen)
    {
        try {
            initComponents();
            this.setSize(new Dimension(450,520));
            setIcon();
            clips = new Environment();
            clips.load("data/RulesDSS.txt");
            clips.reset(); //Load the defFacts, but we dont want to run yet.
            this.allergen = allergen;
            
            jPanel1.setVisible(true);
            this.setVisible(true);
            
            assertUser();
            clipsResult();

            for(int j = 0; j<restUserOrdered.size();j++)
            {
                modelList.addElement(restUserOrdered.get(j).getNameRestaurant());
            }
            
            resName.setCellRenderer(new setColor());
            
            panel_List.setLayout(new BorderLayout());
            panel_List.setPreferredSize(new Dimension(200,100));
            
            panel_List.add(resName, BorderLayout.CENTER);
            resName.addListSelectionListener(this);
            resName.setModel(modelList);
            locationRestaurants("https://www.google.com/maps/d/embed?mid=1B8fHq45Q5PmShrzfGmaOWaCaeCfGb-oU");
            
        } catch (CLIPSLoadException ex) {
            Logger.getLogger(Clips.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CLIPSException ex) {
            Logger.getLogger(Clips.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void assertUser()
    {
        try {
            //This method assert the user to the Clips enviroment, and run the clips program to produce the result
            clips.reset();
            String user = allergen.getUserName();
            String egg = boolean2String(allergen.isEggs());
            String nuts = boolean2String(allergen.isNuts() );
            String gluten = boolean2String(allergen.isGluten());
            String lactose = boolean2String(allergen.isLactose());
            String seafood = boolean2String(allergen.isSeaFood());
            
            String assertIn = "(assert(user(name "+ user +")(nuts "+nuts +")(gluten "+gluten +")(lactose "+ lactose+")(eggs "+ egg+")(seafood "+ seafood+")))";
            clips.eval(assertIn); 
            clips.run();
        } catch (CLIPSException ex) {
            Logger.getLogger(Clips.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void clipsResult()
    {
        try {
            List<FactAddressValue> findAllFactsRU = clips.findAllFacts("restaurantUser");
            List<FactAddressValue> findAllFactsR = clips.findAllFacts("restaurant");
            
            loadRestaurantUsers(findAllFactsRU);
            orderRestUser();
            
            orderRestaurant(loadRestaurants(findAllFactsR));
            
        } catch (CLIPSException ex) {
            Logger.getLogger(Clips.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadRestaurantUsers(List<FactAddressValue> facts )
    {
        //This function returns the restaurants ordered by percentage
        Iterator it = facts.iterator();
        while(it.hasNext())
        {
            FactAddressValue fact = (FactAddressValue)it.next();
            
            String nameUser = fact.getSlotValue("nameUser").toString();
            String nameRestaurant = fact.getSlotValue("nameRestaurant").toString();
            float percentage = Float.parseFloat(fact.getSlotValue("percentage").toString());
            String color = fact.getSlotValue("color").toString();
        
            RestaurantUser restUs = new RestaurantUser(nameUser,nameRestaurant,percentage,color);
            restUserOrdered.add(restUs);
        }
    }
    
    public void orderRestUser()
    {
        for (int i = 0; i < restUserOrdered.size()-1; i++) 
        {
            for(int j = 0; j<restUserOrdered.size()-1; j++)
            {
                if (restUserOrdered.get(j).getPercentage() <= restUserOrdered.get(j+1).getPercentage()) 
                {
                RestaurantUser swapTemp = restUserOrdered.get(j+1);
                restUserOrdered.set(j+1, restUserOrdered.get(j));
                restUserOrdered.set(j,swapTemp);
                }
            }
        
        }        
    }
    
    public ArrayList<Restaurant> loadRestaurants (List<FactAddressValue> facts)
    {
        ArrayList rests = new ArrayList();
        Iterator it = facts.iterator();
        while(it.hasNext())
        {
            FactAddressValue fact = (FactAddressValue) it.next();
            String nameRestaurant = fact.getSlotValue("nameRestaurant").toString();
            String url = fact.getSlotValue("url").toString();
            String url_map = fact.getSlotValue("url_map").toString();
            double latitude = Double.parseDouble(fact.getSlotValue("latitude").toString());
            double longitude = Double.parseDouble(fact.getSlotValue("longitude").toString());;
            Restaurant rest = new Restaurant(nameRestaurant,url,url_map,latitude,longitude);
            rests.add(rest);
        }
        return rests;
    }
    
    public void orderRestaurant(ArrayList<Restaurant>rest)
    {
        Iterator it = restUserOrdered.iterator();
        while(it.hasNext())
        {
           String restaurant = ((RestaurantUser)it.next()).getNameRestaurant();
           restaurants.add(searchRestaurant(restaurant,rest));
        }
        
    }
    
    public Restaurant searchRestaurant(String restName, ArrayList<Restaurant> rests)
    {
        Iterator it = rests.iterator();
        Restaurant rest = null;
        while(it.hasNext())
        {
            rest = (Restaurant) it.next();
            if(rest.getNameRestaurant().equals(restName))
            {
                return rest;
            }
        }
        return rest;
    }
    
    public void locationRestaurants(String urlMap)
   {
       //map.setTitle(urlMap);
       //map.pack();

       BrowserView view = new BrowserView(browser);
       //String urlMap_cut = urlMap.substring(1, urlMap.length() - 1);


       browser.loadURL(urlMap);
       jPanel2.setLayout(new BorderLayout());
       jPanel2.add(view, BorderLayout.CENTER);
       jPanel2.setSize(new Dimension(330,300));
   }
    
   public void locationRestaurantsOutFrame(String urlMap)
   {
      final Browser browser = new Browser();
      BrowserView view = new BrowserView(browser);
      String urlMap_cut = urlMap.substring(1, urlMap.length() - 1);
      //
      final JTextField addressBar = new JTextField(urlMap_cut);
      addressBar.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              browser.loadURL(addressBar.getText());
          }
      });
      JPanel addressPane = new JPanel(new BorderLayout());
      //addressPane.add(new JLabel(" URL: "), BorderLayout.WEST);
      //addressPane.add(addressBar, BorderLayout.CENTER);
      JFrame frame = new JFrame("Google Maps");
      frame.add(addressPane, BorderLayout.NORTH);
      frame.add(view, BorderLayout.CENTER);
      frame.setSize(800, 500);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      browser.loadURL(addressBar.getText());
   }

    public void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("Icons/logo_mni.jpg")));
        this.setTitle("LRG");
    }
    
    public String boolean2String(boolean bool)
    {
        if(bool)
        {
            return "yes";
        }
        else
        {
            return "no";
        }
        
    }
    
    public class setColor extends DefaultListCellRenderer{
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            Component c = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
         
            if (restUserOrdered.get(index).getPercentage() > 75) {
                c.setBackground(Color.green.brighter());
            }else if(restUserOrdered.get(index).getPercentage() < 45) {
                c.setBackground(Color.red.brighter());
            }else{
                c.setBackground(Color.yellow.brighter());
 
            }
            
            return c;
        }
    }
  
    public void valueChanged(ListSelectionEvent lse) {
        if(!lse.getValueIsAdjusting()){
            selectedIndex = resName.getSelectedIndex();
            if(selectedIndex<0){
                selectedIndex=0;
            }
        perc_res_1.setText(Float.toString(restUserOrdered.get(selectedIndex).getPercentage()));
       }    
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        perc_res_1 = new javax.swing.JTextField();
        web_res_1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        panel_List = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(153, 255, 153));
        jPanel1.setMaximumSize(new java.awt.Dimension(450, 500));
        jPanel1.setMinimumSize(new java.awt.Dimension(450, 500));
        jPanel1.setPreferredSize(new java.awt.Dimension(450, 500));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel2.setText("LRG Intellectual property. All Rights Reserved");

        perc_res_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                perc_res_1ActionPerformed(evt);
            }
        });

        web_res_1.setText("Web");
        web_res_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                web_res_1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        jLabel1.setText("Compatibility (%)");

        panel_List.setPreferredSize(new java.awt.Dimension(200, 100));

        javax.swing.GroupLayout panel_ListLayout = new javax.swing.GroupLayout(panel_List);
        panel_List.setLayout(panel_ListLayout);
        panel_ListLayout.setHorizontalGroup(
            panel_ListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        panel_ListLayout.setVerticalGroup(
            panel_ListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel2.setPreferredSize(new java.awt.Dimension(280, 280));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 328, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 280, Short.MAX_VALUE)
        );

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel10.setText("LRG Intellectual property. All Rights Reserved");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8-shutdown-15.png"))); // NOI18N
        try{
            Image img = ImageIO.read(getClass().getResource("back.png"));
            jButton1.setIcon(new ImageIcon(img));
        }catch(Exception ex){
            System.out.println(ex);
        }
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Restaurants");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(panel_List, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(32, 32, 32)
                                            .addComponent(web_res_1))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jLabel1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(perc_res_1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(56, 56, 56)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(45, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addGap(37, 37, 37))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_List, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(perc_res_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addComponent(web_res_1)
                        .addGap(17, 17, 17)))
                .addGap(11, 11, 11)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addGap(120, 120, 120)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void web_res_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_web_res_1ActionPerformed
        String urlMap = restaurants.get(selectedIndex).getUrl_map();
        locationRestaurantsOutFrame(urlMap);
    }//GEN-LAST:event_web_res_1ActionPerformed

    private void perc_res_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_perc_res_1ActionPerformed

    }//GEN-LAST:event_perc_res_1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        EndSession endS = new EndSession(this);
        endS.setVisible(true);
        this.setEnabled(false);

    }//GEN-LAST:event_jButton1ActionPerformed
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel panel_List;
    private javax.swing.JTextField perc_res_1;
    private javax.swing.JButton web_res_1;
    // End of variables declaration//GEN-END:variables

}
