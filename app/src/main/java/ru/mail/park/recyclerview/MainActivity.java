package ru.mail.park.recyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.kohsuke.randname.RandomNameGenerator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final RandomNameGenerator GENERATOR = new RandomNameGenerator();

    private RecyclerView recyclerView;

    private boolean isSpecialOn = false;

    protected boolean isSpecialPosition(int position) {
        int position7 = position % 7;
        return position7 == 1 || position7 == 5 || position7 == 6;
    }

    protected boolean isStaggered() {
        return recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager;
    }

    protected boolean isStaggered3() {
        return isStaggered() &&
                ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).getSpanCount() == 3 &&
                isSpecialOn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DataSource dataSource = new DataSource();
        recyclerView = (RecyclerView) findViewById(R.id.container);
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final View view = getLayoutInflater().inflate(R.layout.card, parent, false);
                return new ItemViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Item item = dataSource.getItem(position);
                ((ItemViewHolder) holder).bind(item);

                final ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();

                if (isStaggered() && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                    final StaggeredGridLayoutManager.LayoutParams staggeredLayoutParams =
                            (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                    staggeredLayoutParams.setFullSpan(isStaggered3() && isSpecialPosition(position));
                    holder.itemView.setLayoutParams(staggeredLayoutParams);
                }
            }

            @Override
            public int getItemCount() {
                return dataSource.getCount();
            }

            @Override
            public int getItemViewType(int position) {
                return 1;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSource.addItem(generateItem());
            }
        });

        findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSource.removeFirst();
            }
        });

        findViewById(R.id.vertical).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }
        });

        findViewById(R.id.horizontal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
            }
        });

        findViewById(R.id.grid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
            }
        });

        findViewById(R.id.grid2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSpecialOn = false;
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            }
        });

        findViewById(R.id.grid3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSpecialOn = true;
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            }
        });
    }

    private Item generateItem() {
        return new Item(GENERATOR.next(), GENERATOR.next(),
                Math.random() > 0.5 ? 1 : 2,
                Math.random() < 0.4 ? 150 : 400
                );
    }

    private static class Item {

        private final String text1;
        private final String text2;
        private final int imgNumber;
        private final int imgSize;

        Item(String text1, String text2, int imgNumber, int imgSize) {
            this.text1 = text1;
            this.text2 = text2;
            this.imgNumber = imgNumber;
            this.imgSize = imgSize;
        }

        public String getText1() {
            return text1;
        }

        public String getText2() {
            return text2;
        }

        public int getImgNumber() {
            return imgNumber;
        }

        public int getImgSize() {
            return imgSize;
        }
    }

    private class DataSource {

        private final List<Item> items = new ArrayList<>();

        public int getCount() {
            return items.size();
        }

        public Item getItem(int position) {
            return items.get(position);
        }

        public void addItem(Item item) {
            items.add(item);
            recyclerView.getAdapter().notifyItemInserted(items.size() - 1);
        }

        public void removeFirst() {
            if (!items.isEmpty()) {
                items.remove(0);
                recyclerView.getAdapter().notifyItemRemoved(0);
            }
        }

    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView text1;
        private final TextView text2;
        private final ImageView img;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.text1 = (TextView) itemView.findViewById(R.id.text1);
            this.text2 = (TextView) itemView.findViewById(R.id.text2);
            this.img = (ImageView) itemView.findViewById(R.id.img);
        }

        public void bind(Item item) {
            text1.setText(item.getText1());
            text2.setText(item.getText2());

            img.setImageResource(item.getImgNumber() == 1 ? R.drawable.i1 : R.drawable.i2);
            img.getLayoutParams().height = item.getImgSize();
            img.requestLayout();
        }

    }

}
