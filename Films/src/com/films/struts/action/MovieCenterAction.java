/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.films.struts.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;

import com.films.domain.Area;
import com.films.domain.Film;
import com.films.domain.Sort;
import com.films.service.inter.IAreaService;
import com.films.service.inter.IFilmService;
import com.films.service.inter.ISortService;
import com.films.service.inter.ITimeTableService;
import com.films.struts.form.MovieForm;
import com.films.utils.MyTools;

/** 
 * MyEclipse Struts
 * Creation date: 10-27-2012
 * 
 * XDoclet definition:
 * @struts.action parameter="flag"
 */
public class MovieCenterAction extends DispatchAction {

	Film film;
	private IFilmService filmService;
	private IAreaService areaService;	
	private ISortService sortService;
	private ITimeTableService timeTableService;
	
	public void setFilmService(IFilmService filmService) {
		this.filmService = filmService;
	}


	public void setAreaService(IAreaService areaService) {
		this.areaService = areaService;
	}


	public void setSortService(ISortService sortService) {
		this.sortService = sortService;
	}

	public void setTimeTableService(ITimeTableService timeTableService) {
		this.timeTableService = timeTableService;
	}

	public ActionForward delMovie(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		String fid = (String) request.getParameter("fid");
		film = (Film) filmService.findById(Film.class, Integer.valueOf(fid));
		if(timeTableService.isTimetable(film.getFid()+"").size()!=0){		
			out.print("<script language='javascript'>alert('Please Delete Film in Showing First!');window.history.back(-1);</script>");
			return null;
		}	
		filmService.delete(film);
		//split page
		String s_pageNow=request.getParameter("pageNow");
		int pageNow=1;
		if(s_pageNow!=null){
			pageNow=Integer.parseInt(s_pageNow);
		}
		request.setAttribute("now", pageNow);
		request.setAttribute("film", filmService.showFilms(10, pageNow));
		int pageCount=timeTableService.getPageCount(10);
		request.setAttribute("pageCount", pageCount);
		return mapping.findForward("showMovie");
	}
	
	public ActionForward editMovie(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		String fid = (String) request.getParameter("fid");
		film = (Film) filmService.findById(Film.class, Integer.valueOf(fid));
		request.setAttribute("area", areaService.getAreas());
		request.setAttribute("sort", sortService.getSort());
		request.setAttribute("movie", film);
		return mapping.findForward("goEditMovie");
	}
	
	public ActionForward editSubmit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		MovieForm movieForm = (MovieForm) form;
		film = (Film) filmService.findById(Film.class, Integer.valueOf(movieForm.getFid()));
		FormFile photo = movieForm.getFilmPhoto();
		if(!photo.getFileName().equals("")){
			String fphoto = MyTools.uploadFilmPhoto(request, photo, film.getFid()+"");
			film.setFphoto(fphoto);
		}else{
			film.setFphoto(film.getFphoto());
		}
		film.setFfilmName(movieForm.getFFilmName());
		film.setFdiretor(movieForm.getFDirector());
		film.setFplay(movieForm.getFPlay());
		film.setFlanguage(movieForm.getFLanguage());
		film.setFlong(Integer.parseInt(movieForm.getFLong()));
		film.setFdate(movieForm.getFDate());
		film.setFtype(movieForm.getFType());
		film.setFintro(movieForm.getFIntro());

		//get sort
		
		Sort sort = (Sort) sortService.findById(Sort.class, Integer.valueOf(movieForm.getSortID()));
		
		//get area
		Area area = (Area) areaService.findById(Area.class, Integer.valueOf(movieForm.getFAid()));
		film.setArea(area);
		film.setSort(sort);
		filmService.update(film);	
		
		//split page
		String s_pageNow=request.getParameter("pageNow");
		int pageNow=1;
		if(s_pageNow!=null){
			pageNow=Integer.parseInt(s_pageNow);
		}
		request.setAttribute("now", pageNow);
		request.setAttribute("film", filmService.showFilms(10, pageNow));
		int pageCount=filmService.getPageCount(10);
		request.setAttribute("pageCount", pageCount);
		return mapping.findForward("showMovie");
	}
}