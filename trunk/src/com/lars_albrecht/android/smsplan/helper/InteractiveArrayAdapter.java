package com.lars_albrecht.android.smsplan.helper;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lars_albrecht.android.smsplan.R;
import com.lars_albrecht.android.smsplan.model.ContactInfo;

public class InteractiveArrayAdapter extends ArrayAdapter<ContactInfo> {

	private final List<ContactInfo> list;
	private final Activity context;

	public InteractiveArrayAdapter(final Activity context, final List<ContactInfo> list) {
		super(context, R.layout.rowbuttonlayout, list);
		this.context = context;
		this.list = list;
	}

	static class ViewHolder {

		protected TextView text;
		protected CheckBox checkbox;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent){
		View view = null;
		if (convertView == null) {
			final LayoutInflater inflator = this.context.getLayoutInflater();
			view = inflator.inflate(R.layout.rowbuttonlayout, null);
			final ViewHolder viewHolder = new ViewHolder();

			viewHolder.text = (TextView) view.findViewById(R.id.label);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked){
					final ContactInfo element = (ContactInfo) viewHolder.checkbox.getTag();
					element.setSelected(buttonView.isChecked());
				}
			});

			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(this.list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(this.list.get(position));
		}
		final ViewHolder holder = (ViewHolder) view.getTag();
		holder.text.setText(this.list.get(position).getDisplayName());
		holder.checkbox.setChecked(this.list.get(position).isSelected());

		return view;
	}
}
