package lasad.shared.communication.objects.parameters;

import java.util.HashMap;
import java.util.Iterator;

public final class OntologyParameterTypeConverter {
	private static final HashMap<String, ParameterTypes> mapping = new HashMap<String, ParameterTypes>() {
		private static final long serialVersionUID = 1237953665979270051L;
		{
			put("elementid", ParameterTypes.ElementId);
			put("elementtype", ParameterTypes.Type);
			put("heading", ParameterTypes.Heading);
			put("width", ParameterTypes.Width);
			put("height", ParameterTypes.Height);
			put("resizable", ParameterTypes.Resizable);
			put("border", ParameterTypes.Border);
			put("background-color", ParameterTypes.BackgroundColor);
			put("font-color", ParameterTypes.FontColor);
			put("minheight", ParameterTypes.MinHeight);
			put("maxheight", ParameterTypes.MaxHeight);
			put("minwidth", ParameterTypes.MinWidth);
			put("maxwidth", ParameterTypes.MaxWidth);
			put("label", ParameterTypes.Label);
			put("options", ParameterTypes.Options);
			put("selectedoption", ParameterTypes.SelectedOption);
			put("endings", ParameterTypes.Endings);
			put("linewidth", ParameterTypes.LineWidth);
			put("linecolor", ParameterTypes.LineColor);
			put("details", ParameterTypes.Details);
			put("detailsonly", ParameterTypes.DetailsOnly);
			put("score", ParameterTypes.Score);
			put("minscore", ParameterTypes.MinScore);
			put("maxscore", ParameterTypes.MaxScore);
			put("text", ParameterTypes.Text);
			put("texttype", ParameterTypes.TextType);
			put("textonimage", ParameterTypes.TextOnImage);
			put("manualadd", ParameterTypes.ManualAdd);
			put("minquantity", ParameterTypes.MinQuantity);
			put("maxquantity", ParameterTypes.MaxQuantity);
			put("longlabel", ParameterTypes.LongLabel);
			put("startrow", ParameterTypes.StartRow);
			put("endrow", ParameterTypes.EndRow);
			put("startpoint", ParameterTypes.StartPoint);
			put("endpoint", ParameterTypes.EndPoint);
			put("source", ParameterTypes.Source);
			put("windowheight", ParameterTypes.WindowHeight);
			put("windowwidth", ParameterTypes.WindowWidth);
			put("editable", ParameterTypes.Editable);
			put("defaultURL", ParameterTypes.DefaultURL);
			put("configbutton", ParameterTypes.ConfigButton);
			put("canbegrouped", ParameterTypes.CanBeGrouped);
			put("connectsgroup", ParameterTypes.ConnectsGroup);
		}
	};

	private OntologyParameterTypeConverter() {

	}

	public static ParameterTypes getParameterByOntologyValue(String ontologyValue) throws Exception {
		if (mapping.containsKey(ontologyValue)) {
			return mapping.get(ontologyValue);
		} else {
			throw new Exception("Mapping does not contain the ontology key");
		}
	}

	public static String getOntologyValueByParamter(ParameterTypes parameter) throws Exception {

		if (mapping.values().contains(parameter)) {
			boolean found = false;
			Iterator<String> iter = mapping.keySet().iterator();
			String key = null;
			while (!found && iter.hasNext()) {
				key = iter.next();
				if (mapping.get(key) == parameter) {
					found = true;
				}
			}
			return key;

		} else {
			throw new Exception("Mapping does not contain the parameter value");
		}
	}
}
