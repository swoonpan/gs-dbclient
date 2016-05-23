package it.egeos.geoserver.dbmanagers;

import it.egeos.geoserver.restmanagers.tuples.VTGeometryTuple;
import it.egeos.geoserver.restmanagers.tuples.VTParameterTuple;

import java.util.List;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.WMSStoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geotools.geometry.jts.Geometries;
import org.geotools.jdbc.RegexpValidator;
import org.geotools.jdbc.VirtualTable;
import org.geotools.jdbc.VirtualTableParameter;
import org.geotools.util.Converters;

public class Factory {
    private Catalog cat;

    public Factory(Catalog cat) {
        super();
        this.cat = cat;
    }
    
    public WorkspaceInfo newWorkspace(String name){
        WorkspaceInfo ws = cat.getFactory().createWorkspace();
        ws.setName(name);       
        return ws;
    }

    public NamespaceInfo newNamespace(String prefix,String uri) {
        NamespaceInfo ns = cat.getFactory().createNamespace();
        ns.setPrefix(prefix);
        ns.setURI(uri);        
        return ns;
    }

    public WMSStoreInfo newWmsStore(WorkspaceInfo ws,final String name,final String url,final String usr, final String pwd) {
        WMSStoreInfo st = cat.getFactory().create(WMSStoreInfo.class);
        st.setName(name);
        st.setCapabilitiesURL(url);
        st.setWorkspace(ws);
        st.setEnabled(true);
        st.setUsername(usr);
        st.setPassword(pwd);
        return st;
    }
    
    public LayerGroupInfo newLayerGroup(String workspace, String name){
        LayerGroupInfo lg=cat.getFactory().createLayerGroup();
        lg.setWorkspace(cat.getWorkspaceByName(workspace));
        lg.setName(name);
        return lg;
    }
 
    public FeatureTypeInfo newFeatureTypeInfo(String workspace,String store,String name){
        FeatureTypeInfo ft = cat.getFactory().createFeatureType();
        ft.setStore(cat.getStoreByName(workspace, store, StoreInfo.class));
        ft.setName(name);
        ft.setNativeName(name);
        ft.setTitle(name);
        //TODO: may be others info
        return ft;
    }
    
    public VirtualTable newVirtualTable(String name, String sql,List<VTGeometryTuple> geoms,List<VTParameterTuple> pars){
        VirtualTable vt=new VirtualTable(name, sql);
        for(VTGeometryTuple g:geoms)
            vt.addGeometryMetadatata(g.getName(),Geometries.getForName(g.getGeometryType()).getBinding(),Converters.convert(g.getSrid(), Integer.class));    
        for(VTParameterTuple p:pars)
            vt.addParameter(new VirtualTableParameter(p.getName(), p.getDefaultValue(),new RegexpValidator(p.getRegexpValidator())));
        return vt;
    }
}