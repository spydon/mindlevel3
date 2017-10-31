package net.mindlevel.model;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.pchmn.materialchips.model.ChipInterface;

import net.mindlevel.model.User;

public class UserChip implements ChipInterface {

    private User user;

    public UserChip(User user) {
        super();
        this.user = user;
    }

    @Override
    public Object getId() {
        return user.username;
    }

    @Override
    public Uri getAvatarUri() {
        return user.image == null ? null : Uri.parse(user.image);
    }

    @Override
    public Drawable getAvatarDrawable() {
        //Uri uri = getAvatarUri();
        //InputStream is = null;
        //try {
        //    is = context.getContentResolver().openInputStream(uri);
        //    return Drawable.createFromStream(is, uri.toString() );
        //} catch (FileNotFoundException e) {
        //    return context.getResources().getDrawable(R.drawable.avatar, context.getTheme());
        //} finally {
        //    if (is != null) {
        //        try {
        //            is.close();
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
        //    }
        //}
        // TODO: Fix thumbnails
        return null;
    }

    @Override
    public String getLabel() {
        return user.username;
    }

    @Override
    public String getInfo() {
        return user.description;
    }
}
