package org.fleen.forsythia.app.grammarEditor.editor_Jig.gridOverlayPainter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.fleen.forsythia.app.grammarEditor.GE;
import org.fleen.forsythia.app.grammarEditor.editor_Jig.Editor_Jig;
import org.fleen.forsythia.app.grammarEditor.editor_Jig.JigSectionEditingModel;
import org.fleen.forsythia.app.grammarEditor.editor_Jig.graph.GEdge;
import org.fleen.forsythia.app.grammarEditor.editor_Jig.graph.GVertex;
import org.fleen.forsythia.app.grammarEditor.util.UI;
import org.fleen.geom_2D.DPoint;
import org.fleen.geom_2D.DPolygon;
import org.fleen.geom_2D.GD;
import org.fleen.geom_Kisrhombille.KPolygon;

public class GridOverlayPainter{
  
  public void paint(Graphics2D graphics,int w,int h,double scale,double centerx,double centery){
    graphics.setRenderingHints(UI.RENDERING_HINTS);
    GE.editor_jig.model.viewgeometrycache.update(w,h,scale,centerx,centery);
    if(GE.editor_jig.mode==Editor_Jig.MODE_EDITSECTIONS){
      renderJigModel_EditSections(graphics);
    }else{//GE.editor_jig.mode==Editor_Jig.MODE_EDITGEOMETRY
      renderJigModel_EditGeometry(graphics);}}
  
  /*
   * ################################
   * RENDER JIG MODEL FOR EDIT GEOMETRY MODE
   * ################################
   */
  
  private void renderJigModel_EditGeometry(Graphics2D graphics){
    fillSections_EditGeometry(graphics);
    strokeGraphEdges_EditGeometry(graphics);
    renderVertices_EditGeometry(graphics);
  }
  
  private void fillSections_EditGeometry(Graphics2D graphics){
    Color color;
    Path2D path;
    for(KPolygon m:GE.editor_jig.model.rawgraph.getDisconnectedGraph().getUndividedPolygons()){
      color=UI.EDITJIG_EDITGEOMETRY_HOSTMETAGONFILLCOLOR;
      path=GE.editor_jig.model.viewgeometrycache.getPath(m);
      graphics.setPaint(color);
      graphics.fill(path);}}
  
  private void strokeGraphEdges_EditGeometry(Graphics2D graphics){
    graphics.setStroke(UI.GRID_DRAWINGSTROKE);
    graphics.setPaint(UI.EDITJIG_EDITGEOMETRY_STROKECOLOR);
    Iterator<GEdge> i=GE.editor_jig.model.rawgraph.edges.iterator();
    GEdge e;
    double[] p0,p1;
    Path2D path=new Path2D.Double();
    //System.out.println("edge count:"+Q.editor_createjig.graph.getEdgeCount());
    while(i.hasNext()){
      e=i.next();
      p0=GE.editor_jig.model.viewgeometrycache.getPoint(e.v0.kvertex);
      p1=GE.editor_jig.model.viewgeometrycache.getPoint(e.v1.kvertex);
      path.reset();
      path.moveTo(p0[0],p0[1]);
      path.lineTo(p1[0],p1[1]);
      graphics.draw(path);}}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * RENDER VERTICES
   * ++++++++++++++++++++++++++++++++
   */
  
  private void renderVertices_EditGeometry(Graphics2D graphics){
    renderDefaultVertices(graphics,UI.EDITJIG_EDITGEOMETRY_STROKECOLOR);
    renderHeadDecorations(graphics);}
  
  private void renderDefaultVertices(Graphics2D graphics,Color color){
    graphics.setPaint(color);
    float span=UI.GRID_DEFAULTVERTEXSPAN;
    double[] p;
    Ellipse2D dot=new Ellipse2D.Double();
    for(GVertex v:GE.editor_jig.model.rawgraph.vertices){
      p=GE.editor_jig.model.viewgeometrycache.getPoint(v.kvertex);
      dot.setFrame(p[0]-span/2,p[1]-span/2,span,span);
      graphics.fill(dot);}}
  
  private void renderHeadDecorations(Graphics2D graphics){
    graphics.setStroke(UI.EDITJIG_EDITGEOMETRY_HEADVERTEXDECORATIONSTROKE);
    int span=UI.EDITJIG_EDITGEOMETRY_HEADVERTEXDECORATIONSPAN;
    double[] p;
    if(GE.editor_jig.connectedhead!=null){
      p=GE.editor_jig.model.viewgeometrycache.getPoint(GE.editor_jig.connectedhead);
      graphics.setPaint(UI.EDITJIG_EDITGEOMETRY_CONNECTEDHEADVERTEXDECORATIONCOLOR);
      graphics.drawOval(((int)p[0])-span/2,((int)p[1])-span/2,span,span);
    }else if(GE.editor_jig.unconnectedhead!=null){
      p=GE.editor_jig.model.viewgeometrycache.getPoint(GE.editor_jig.unconnectedhead);
      graphics.setPaint(UI.EDITJIG_EDITGEOMETRY_UNCONNECTEDHEADVERTEXDECORATIONCOLOR);
      graphics.drawOval(((int)p[0])-span/2,((int)p[1])-span/2,span,span);}}
  
  /*
   * ################################
   * RENDER JIG MODEL FOR EDIT SECTIONS MODE
   * ################################
   */
  
  private void renderJigModel_EditSections(Graphics2D graphics){
    fillSections_EditSections(graphics);
    strokePolygonEdges_EditSections(graphics);
    renderGlyphs_EditSections(graphics);}
  
  /*
   * Fill color reflects chorus index. A rainbow.
   * 0 is red, 1 is orangyred and so on 
   */
  private void fillSections_EditSections(Graphics2D graphics){
    int colorindex;
    Color color;
    Path2D path;
    for(JigSectionEditingModel m:GE.editor_jig.model.sections){
      colorindex=m.chorus;
      color=UI.EDITJIG_EDITSECTIONS_SECTIONFILL[colorindex%UI.EDITJIG_EDITSECTIONS_SECTIONFILL.length];
      path=GE.editor_jig.model.viewgeometrycache.getPath(m.getPolygon());
      graphics.setPaint(color);
      graphics.fill(path);}}
  
  /*
   * focus section gets stroked in one color, all of the unfocus sections get stroked in another
   * colors probably match for glyph
   */
  private void strokePolygonEdges_EditSections(Graphics2D graphics){
    graphics.setStroke(UI.GRID_DRAWINGSTROKE);
    graphics.setPaint(UI.EDITJIG_EDITSECTIONS_UNFOCUSSTROKECOLOR);
    Path2D path;
    for(JigSectionEditingModel m:GE.editor_jig.model.sections){
      if(m==GE.editor_jig.focussection)continue;
      path=GE.editor_jig.model.viewgeometrycache.getPath(m.getPolygon());
      graphics.draw(path);}
    //focus
    graphics.setPaint(UI.EDITJIG_EDITSECTIONS_FOCUSSTROKECOLOR);
    path=GE.editor_jig.model.viewgeometrycache.getPath(GE.editor_jig.focussection.getPolygon());
    graphics.draw(path);}
  
  /*
   * ################################
   * RENDER GLYPHS FOR EDIT SECTIONS
   * The focus polygon gets a system of glyphs within its edge that indicate
   * the form of the anchor. That is, v0 and twist.
   * we indicate vertex0 with a dot
   * we indicate twist with a long bendy arrow 
   * ################################
   */
  
  //TODO thse should be params in UI
  private static final double GRIDOVERLAYPAINTER_GLYPHINSET=12;
  
  private void renderGlyphs_EditSections(Graphics2D graphics){
    //get non-focus section polygons
    List<DPolygon> nonfocussections=new ArrayList<DPolygon>();
    for(JigSectionEditingModel section:GE.editor_jig.model.sections)
      if(section!=GE.editor_jig.focussection)
        nonfocussections.add(GE.editor_jig.model.viewgeometrycache.getDPolygon(section.getPolygon()));
    //get focus section polygon
    DPolygon focussection=GE.editor_jig.model.viewgeometrycache.getDPolygon(GE.editor_jig.focussection.getPolygon());
    //render non-focus section polygons
    for(DPolygon nonfocussection:nonfocussections)
      renderGlyphs(graphics,nonfocussection,UI.EDITJIG_EDITSECTIONS_UNFOCUSSTROKECOLOR);
    //render focus section polygon
    renderGlyphs(graphics,focussection,UI.EDITJIG_EDITSECTIONS_FOCUSSTROKECOLOR);}
  
  private void renderGlyphs(Graphics2D graphics,DPolygon polygon,Color color){
    System.out.println("render glyphs");
    GlyphSystemModel glyphsystemmodel=new GlyphSystemModel(
      polygon,
      GRIDOVERLAYPAINTER_GLYPHINSET);
    if(glyphsystemmodel.isValid()){
      //render v0 dot
      renderV0Dot(graphics,glyphsystemmodel,color);
      //render arrow shaft
      graphics.setStroke(UI.GRID_DRAWINGSTROKE);
      graphics.setPaint(color);
      Path2D path =getPath2D(glyphsystemmodel.glyphpath);
      graphics.draw(path);
      //render arrow head
      renderArrowHead(graphics,glyphsystemmodel,color);}}
    
  /*
   * ++++++++++++++++++++++++++++++++
   * RENDER V0 DOT
   * ++++++++++++++++++++++++++++++++
   */
  
  private static final double V0DOTRADIUS=0.6;
  
  private void renderV0Dot(Graphics2D graphics,GlyphSystemModel glyphsystemmodel,Color color){
    double dotradius=V0DOTRADIUS*GRIDOVERLAYPAINTER_GLYPHINSET;
    DPoint pv0=glyphsystemmodel.getV0DotPoint();
    graphics.setPaint(color);
    Ellipse2D dot=new Ellipse2D.Double(pv0.x-dotradius,pv0.y-dotradius,dotradius*2,dotradius*2);
    graphics.fill(dot);}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * RENDER ARROW HEAD
   * ++++++++++++++++++++++++++++++++
   */
  
  //in terms of inset
  private static final double ARROWLENGTH=2.5,ARROWWIDTH=1.2;
  
  private void renderArrowHead(Graphics2D graphics,GlyphSystemModel glyphsystemmodel,Color color){
    graphics.setPaint(color);
    DPoint 
      p0=glyphsystemmodel.glyphpath.get(glyphsystemmodel.glyphpath.size()-2),
      p1=glyphsystemmodel.glyphpath.get(glyphsystemmodel.glyphpath.size()-1);
    double forward=p0.getDirection(p1);
    DPoint 
      forewardpoint=p1.getPoint(forward,ARROWLENGTH*GRIDOVERLAYPAINTER_GLYPHINSET),
      leftpoint=p1.getPoint(GD.normalizeDirection(forward-GD.HALFPI),ARROWWIDTH*GRIDOVERLAYPAINTER_GLYPHINSET/2),
      rightpoint=p1.getPoint(GD.normalizeDirection(forward+GD.HALFPI),ARROWWIDTH*GRIDOVERLAYPAINTER_GLYPHINSET/2);
    Path2D triangle=new Path2D.Double();
    triangle.moveTo(leftpoint.x,leftpoint.y);
    triangle.lineTo(forewardpoint.x,forewardpoint.y);
    triangle.lineTo(rightpoint.x,rightpoint.y);
    triangle.closePath();
    graphics.fill(triangle);}
  
  private Path2D getPath2D(List<DPoint> points){
    Path2D path2d=new Path2D.Double();
    DPoint p=points.get(0);
    path2d.moveTo(p.x,p.y);
    int s=points.size();
    for(int i=1;i<s;i++){
      p=points.get(i);
      path2d.lineTo(p.x,p.y);}
    return path2d;}
  
}