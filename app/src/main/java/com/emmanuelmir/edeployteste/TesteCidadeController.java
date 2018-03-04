package com.emmanuelmir.edeployteste;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Emmanuel Junior on 02/03/2018.
 */

public class TesteCidadeController extends RecyclerView.Adapter<TesteCidadeController.TesteCidadeControllerViewHolder> {

    private TesteCidadeModel[] mCidadesData;
    private ArrayList<TesteCidadeModel> mCidadesFiltered = new ArrayList<>();
    private OnPontosClickListener onPontosClickListener;

    public class TesteCidadeControllerViewHolder extends RecyclerView.ViewHolder{
        public final TextView mCidade;
        public final TextView mEstado;
        public final TextView verPontosTv;

        public TesteCidadeControllerViewHolder(View itemView) {
            super(itemView);
            mCidade = (TextView) itemView.findViewById(R.id.cidade_tv);
            mEstado = (TextView) itemView.findViewById(R.id.estado_tv);
            verPontosTv = (TextView)itemView.findViewById(R.id.visualizar_pontos);
        }
    }

    @Override
    public TesteCidadeControllerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_cidade_viewholder;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImediatamente = false;

        View itemView = inflater.inflate(layoutIdForListItem,parent,attachImediatamente);

        return new TesteCidadeControllerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TesteCidadeControllerViewHolder holder, final int position) {
        final int positionData = position; //Tem que ser definido aqui para que seja guardado como o endereço da holder.
        String cidadeData = mCidadesData[position].getNome();
        String estadoData = mCidadesData[position].getEstado();
        holder.mCidade.setText(cidadeData);
        holder.mEstado.setText(estadoData);
        holder.verPontosTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TesteCidadeView activity = (TesteCidadeView) v.getContext();
                activity.startAsync(mCidadesData[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(null == mCidadesData) return 0;
        return mCidadesData.length;
    }

    public void setmCidadesData(TesteCidadeModel[] cidadesDataNew, CharSequence estado, CharSequence cidade){
        mCidadesData = cidadesDataNew;
        publishResults(estado, cidade);
        notifyDataSetChanged();
    }


    protected TesteCidadeModel[] performFiltering(CharSequence charEstado, CharSequence charCidade) {

        String stringCidade = charCidade.toString().toLowerCase();
        String stringEstado = charEstado.toString().toLowerCase();

                if (stringCidade.isEmpty() && stringEstado.isEmpty()){
                    return mCidadesData;
                } else {
                    if(!mCidadesFiltered.isEmpty())mCidadesFiltered.clear();
                    for (TesteCidadeModel i : mCidadesData) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (i.getNome().toLowerCase().contains(stringCidade) && !stringCidade.equals("")){
                            mCidadesFiltered.add(i);
                        }
                        if(i.getEstado().toLowerCase().contains(stringEstado) && !stringEstado.equals("")){
                            mCidadesFiltered.add(i);
                        }

                    }


                }

        mCidadesData = new TesteCidadeModel[mCidadesFiltered.size()];
        mCidadesData = mCidadesFiltered.toArray(mCidadesData);
        return mCidadesData;

    }

    public static interface OnPontosClickListener {
        public void onPontosClick(View v, long id);
    }

    public void publishResults(CharSequence estado, CharSequence cidade) {
        performFiltering(cidade, estado);
    }

}
