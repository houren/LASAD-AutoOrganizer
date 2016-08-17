package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.widget;

import java.util.List;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.CustomDualListFieldEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.CustomDualListFieldEventType;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.widget.form.DualListField;

/**
 * Custom implementation of {@link DualListField} throws events for MoveSelectedDown, MoveSelectedUp,
 * AddSelected and RemoveSelected
 * @author Anahuac
 *
 * @param <D>
 */
public class CustomDualListField<D extends ModelData> extends CustomDualListField2<D> {
	
//	public static final int leftButton = 0x1;
//	public static final int rightButton = 0x2;
//	public static final int allLeftButton = 0x3;
//	public static final int allRightButton = 0x4;
//	public static final int upButton = 0x5;
//	public static final int downButton = 0x6;
//	
//	List<Integer> buttonsToRemoveList;
//	
//	public CustomDualListField(){
//		super();
//	}
	
	public CustomDualListField(List<Integer> buttonsToRemoveList){
		super(buttonsToRemoveList);
//		this.buttonsToRemoveList = buttonsToRemoveList;
	}
	
	@Override
	protected void onButtonDown(IconButtonEvent be){
	    super.onButtonDown(be);
	    CustomDualListFieldEvent se = new CustomDualListFieldEvent(CustomDualListFieldEventType.MoveSelectedDown);
	    fireEvent(CustomDualListFieldEventType.MoveSelectedDown, se);
	}
	@Override
	protected void onButtonUp(IconButtonEvent be){
		super.onButtonUp(be);
		CustomDualListFieldEvent se = new CustomDualListFieldEvent(CustomDualListFieldEventType.MoveSelectedUp);
	    fireEvent(CustomDualListFieldEventType.MoveSelectedUp, se);
	}
	@Override
	protected void onButtonRight(IconButtonEvent be){
		super.onButtonRight(be);
		CustomDualListFieldEvent se = new CustomDualListFieldEvent(CustomDualListFieldEventType.AddSelected);
	    fireEvent(CustomDualListFieldEventType.AddSelected, se);
	}@Override
	protected void onButtonLeft(IconButtonEvent be){
		super.onButtonLeft(be);
		CustomDualListFieldEvent se = new CustomDualListFieldEvent(CustomDualListFieldEventType.RemoveSelected);
	    fireEvent(CustomDualListFieldEventType.RemoveSelected, se);
	}
	
//	@Override
//    protected void onRender(Element target, int index) {
//		super.onRender(target, index);
//		//allRight.disable();
//		
//		if(buttonsToRemoveList != null){
//			for(Integer val:buttonsToRemoveList){
//				switch(val.intValue()){
//					case leftButton:{
//						buttonBar.remove(left);
//						break;
//					}
//					case rightButton:{
//						buttonBar.remove(right);
//						break;
//					}
//					case allLeftButton:{
//						buttonBar.remove(allLeft);
//						break;
//					}
//					case allRightButton:{
//						buttonBar.remove(allRight);
//						break;
//					}
//					case upButton:{
//						buttonBar.remove(up);
//						break;
//					}
//					case downButton:{
//						buttonBar.remove(down);
//						break;
//					}
//				}
//			}
//		}
//    }
}
