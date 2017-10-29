package com.loyer.flashchatnewfirebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by loyer on 10.10.2017.
 */

    //baseadapterden kalıtım almamızın sebebi listviewi görsel olarak
    //düzenleyeceğiz burda bize yardımcı olacak

     public class ChatListAdapter extends BaseAdapter {

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mSnapshotList;

    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            //arraylistleri ekleyeceğimiz yer
            //önce geri çağrı yoluyla aldığımız SnapShot verilerini SnapShots koleksiyonlarımıza ekleyeceğiz.
            mSnapshotList.add(dataSnapshot);
            //Dizi listesine yapılan her eklemeden sonra ListView'e kendisini yenilemesi gerektiğini bildirmeliyiz.
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public ChatListAdapter(Activity activity, DatabaseReference reference , String name){
        mActivity = activity;
        mDisplayName = name;
        mDatabaseReference = reference.child("messages");

        mDatabaseReference.addChildEventListener(mListener);
        mSnapshotList = new ArrayList<>();



    }
    //mesaj satırımızı bir noktada programlı olarak biçimlendirmek isteyeceğiz
    // böylece düzen parametreleri için başka bir alan ekleyeceğiz
    static class ViewHolder{

        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }


    @Override
    public int getCount() {
       //Arraylistimizin sizeını çekiyoruz
        return mSnapshotList.size();
    }


    //childeventlistenerın onchlidadd metodunda yaptığımız değişiklik sonucunda bize bildirim verecek
    //bu bildim mesajını
    //ilgili anlık mesajı snapshot listeden çıkarıcaz
    @Override
    public InstantMessage getItem(int position) {

        //Belirli SnapShot'ı almak için dizi listesinden get komutunu kullanalım
        DataSnapshot snapshot = mSnapshotList.get(position);
        return snapshot.getValue(InstantMessage.class);
    }



    @Override
    public long getItemId(int i) {
        return 0;
    }

    //VievHolder oluşturup bunu bu komutla kullanmamızın sebei
    //çok fazla mesaj olduğunda lag oalbilir
    //telefon kasabilir, çok fazla ram kullanılabilir
    //ve bu bize kötü kullanıcı deneyimi demektir
    //bu şekilde xml dosyasını atarak daha kullanışlı
    //ve daha performanslı bir mesaj listeleme yapabiliriz çünkü
    //sürekli obje yaratıp silmek zorunda kalmayacaz
    //yani aldığımız her bir görünümü verilerle doldurup
    //eski satırları yenileyen ve bunları yeni öğe olarak aktarmaya çalışacağız
    //sıfırdan yeni satır yaratmak yerine var olanı kullanıcaz
    @Override// i pozisyon converview xml dosyamızı temsil edecek viewgrouğta görüntülecenk yer
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        //bağdaştırıcıyı görüntülemek için get komutunda if ile
        // kullanılabilir bir satır olup olmadığını kontrol ediyoruz.
        //yoksa;
     if(convertView == null)
     {

         LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         //xml dosyasından yeni bir satır yaratıyoruz
         convertView = inflater.inflate(R.layout.chat_msg_row,viewGroup,false);
         //Ve depoluyoruz
         final ViewHolder holder = new ViewHolder();
         holder.authorName = (TextView) convertView.findViewById(R.id.author);
         holder.body = (TextView) convertView.findViewById(R.id.message);
         holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();

         //yukarıda depoladığımız şeyleri viewholder şeklinde
         //convertviewe ekliyoruz
         convertView.setTag(holder);
     }

     //mesajın pozisyonunu aldık
     final InstantMessage message = getItem(i);
        //mevcutviewHolderı geri alıyoruz
     final ViewHolder holder = (ViewHolder) convertView.getTag();

        Boolean isMe = message.getAuthor().equals(mDisplayName);
        setChatRowAppereance(isMe,holder );

        //anlık mesajdaki verileri kullanarak viewHolderın textViewini buna uygun olarak ayarladık.
        String author = message.getAuthor();
        holder.authorName.setText(author);

        String msg = message.getMessage();
        holder.body.setText(msg);
        //eğer kullanıcının kullanabileceği satırlar varsa
        //return ediyoruz
        return convertView;
    }
        //sohbet mesajlarının görünümü ayarlayacağız
    private void setChatRowAppereance(Boolean isItMe, ViewHolder holder){

        //mesaj kullanıcıya aitse sağ satıra yaslayacağız tüm mesajlarını
        if(isItMe){
            holder.params.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.GREEN);
            holder.body.setBackgroundResource(R.drawable.bubble2);

        }else{//karşıdan birisine aitse sola yaslıyoruz
            holder.params.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble1);

        }
        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);

    }

    public void cleanUp()
    {
        mDatabaseReference.removeEventListener(mListener);
    }
}
