package vc908.stickerfactory.ui.fragment;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vc908.stickerfactory.EmojiSettingsBuilder;
import vc908.stickerfactory.R;
import vc908.stickerfactory.StickersManager;
import vc908.stickerfactory.StorageManager;
import vc908.stickerfactory.emoji.Emoji;
import vc908.stickerfactory.emoji.EmojiList;
import vc908.stickerfactory.ui.OnEmojiBackspaceClickListener;
import vc908.stickerfactory.ui.OnStickerSelectedListener;
import vc908.stickerfactory.ui.view.SquareImageView;
import vc908.stickerfactory.ui.view.SquareTextView;
import vc908.stickerfactory.utils.CompatUtils;
import vc908.stickerfactory.utils.Utils;

/**
 * Fragment with emoji list
 *
 * @author Dmitry Nezhydenko
 */
public class EmojiFragment extends Fragment {

    private List<OnStickerSelectedListener> stickerSelectedListeners = new ArrayList<>();
    private View layout;
    private int size;
    private OnEmojiBackspaceClickListener emojiBackspaceClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null) {
            layout = inflater.inflate(R.layout.sp_fragment_emoji_list, container, false);
            RecyclerView rv = (RecyclerView) layout.findViewById(R.id.recycler_view);
            rv.setHasFixedSize(true);
            size = 28;//(int) Utils.dp(getEmojiSize(People.DATA[0], 10));
            // calculate emoji columns count
            int minItemSize = Utils.dp(48, getContext());
            int backspaceColumnSize = getResources().getDimensionPixelSize(R.dimen.sp_backspace_column_width);
            int itemsSpanCount = (int) (Math.floor((Utils.getScreenWidthInPx(getContext()) - backspaceColumnSize) / minItemSize));
            int itemWidth = (int) Math.floor(((float) Utils.getScreenWidthInPx(getContext())) / itemsSpanCount);
            int columnsCount = (Utils.getScreenWidthInPx(getContext()) / itemWidth);
            GridLayoutManager lm = new GridLayoutManager(getActivity(), columnsCount);
            rv.setLayoutManager(lm);
            if (StickersManager.getEmojiSettingsBuilder() == null) {
                EmojiAdapter adapter = new EmojiAdapter();
                adapter.setHasStableIds(true);
                rv.setAdapter(adapter);
            } else {
                rv.setAdapter(new CustomEmojiAdapter(getContext(), stickerSelectedListeners));
            }
            ImageView backspaceView = (ImageView) layout.findViewById(R.id.clear_button);
            backspaceView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.sp_stickers_backspace));
            backspaceView.getLayoutParams().height = (Utils.getScreenWidthInPx(getContext()) - backspaceColumnSize) / itemsSpanCount;
            backspaceView.setOnClickListener(v -> {
                if (emojiBackspaceClickListener != null) {
                    emojiBackspaceClickListener.onEmojiBackspaceClicked();
                }
            });
            backspaceView.setOnLongClickListener(l -> {
                Utils.copyToClipboard(getActivity(), StorageManager.getInstance().getUserID());
                return true;
            });
            CompatUtils.setBackgroundDrawable(backspaceView, Utils.createSelectableBackground(getActivity()));
        }
        return layout;
    }

    /**
     * Add emoji selected listener
     *
     * @param stickerSelectedListener Listener
     */
    public void addStickerSelectedListener(OnStickerSelectedListener stickerSelectedListener) {
        stickerSelectedListeners.add(stickerSelectedListener);
    }


    /**
     * Set click listener for backspace icon
     *
     * @param listener Backspace click listener
     */
    public void setOnBackspaceClickListener(OnEmojiBackspaceClickListener listener) {
        this.emojiBackspaceClickListener = listener;
    }

    /**
     * Adapter for emoji list
     */
    private class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {

        private final Emoji[] data;

        public EmojiAdapter() {
            data = EmojiList.DATA;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new SquareTextView(getActivity());
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            tv.setTextSize(size);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
            CompatUtils.setBackgroundDrawable(tv, Utils.createSelectableBackground(tv.getContext()));
            return new ViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String code = data[position].getEmoji();
            holder.tv.setText(code);
            holder.code = code;
        }

        @Override
        public int getItemCount() {
            return data.length;
        }

        /**
         * Emoji item iv holder
         */
        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tv;
            private String code;

            public ViewHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView;
                tv.setOnClickListener(v -> {
                    if (stickerSelectedListeners.size() > 0) {
                        for (OnStickerSelectedListener listener : stickerSelectedListeners) {
                            listener.onEmojiSelected(code);
                        }
                    }
                });
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    private static class CustomEmojiAdapter extends RecyclerView.Adapter<CustomEmojiAdapter.ViewHolder> {

        private final EmojiSettingsBuilder settings;
        private final ArrayList<Pair<String, String>> data = new ArrayList<>();
        private final int padding;
        private final AssetManager assetManager;
        private final Context mContext;
        private final List<OnStickerSelectedListener> mStickerSelectedListeners;

        CustomEmojiAdapter(Context context, List<OnStickerSelectedListener> stickerSelectedListeners) {
            this.mContext = context;
            this.mStickerSelectedListeners = stickerSelectedListeners;
            padding = Utils.dp(8, context);
            settings = StickersManager.getEmojiSettingsBuilder();
            if (settings != null && settings.getCustomEmojiMap() != null) {
                for (Map.Entry<String, String> entry : settings.getCustomEmojiMap().entrySet()) {
                    data.add(new Pair<>(entry.getKey(), entry.getValue()));
                }
            }
            assetManager = context.getAssets();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView view = new SquareImageView(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setPadding(padding, padding, padding, padding);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            try {
                Pair<String, String> item = data.get(position);
                switch (settings.getResourceLocation()) {
                    case DRAWABLE:
                        int imageId = mContext.getResources().getIdentifier(item.second, "drawable", mContext.getPackageName());
                        holder.iv.setImageResource(imageId);
                        break;
                    case ASSETS:
                        InputStream is = assetManager.open(settings.getAssetsFolder() + item.second);
                        holder.iv.setImageBitmap(BitmapFactory.decodeStream(is));
                    default:
                }
                holder.code = item.first;

            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView iv;
            private String code;

            public ViewHolder(View itemView) {
                super(itemView);
                iv = (ImageView) itemView;
                iv.setOnClickListener(v -> {
                    if (mStickerSelectedListeners.size() > 0) {
                        for (OnStickerSelectedListener listener : mStickerSelectedListeners) {
                            listener.onStickerSelected(code);
                        }
                    }
                });
            }
        }
    }
}
