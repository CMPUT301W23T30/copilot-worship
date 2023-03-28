package com.example.qrhunter;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class QRCodeComment implements Parcelable {
    public QRCode qrCode;
    public String comment;

    public QRCodeComment(QRCode qrCode, String comment){
        this.qrCode = qrCode;
        this.comment = comment;
    }

    protected QRCodeComment(Parcel in) {
        qrCode = in.readParcelable(QRCode.class.getClassLoader());
        comment = in.readString();
    }

    public static final Creator<QRCodeComment> CREATOR = new Creator<QRCodeComment>() {
        @Override
        public QRCodeComment createFromParcel(Parcel in) {
            return new QRCodeComment(in);
        }

        @Override
        public QRCodeComment[] newArray(int size) {
            return new QRCodeComment[size];
        }
    };

    public QRCode getQrCode(){return qrCode;}
    public void setQrCode(QRCode qrCode){this.qrCode=qrCode;}

    public String getComment(){return comment;}
    public void setComment(QRCode qrCode){this.comment=comment;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(qrCode, flags);
        dest.writeString(comment);
    }
}
