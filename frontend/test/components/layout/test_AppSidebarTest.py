pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import Page, expect

LOGIN_URL = "http://localhost:3000/login"
HOME_URL = "http://localhost:3000/"
SIDEBAR_SELECTOR = ".app-sidebar"

TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}
TUTOR_CREDENTIALS = {"userId": "A001", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, goto_url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    # 修正跳转首页逻辑
    if goto_url is None:
        goto_url = HOME_URL
    page.goto(goto_url)
    expect(page.locator(SIDEBAR_SELECTOR)).to_be_visible(timeout=10000)
    return browser, context, page

def test_teacher_sidebar(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS)
    sidebar = page.locator(SIDEBAR_SELECTOR)
    expect(sidebar.locator("text=课程中心")).to_be_visible()
    expect(sidebar.locator("text=题库中心")).to_be_visible()
    expect(sidebar.locator("text=创建课程")).to_be_visible()
    expect(sidebar.locator("text=私密知识库设置")).to_be_visible()
    expect(sidebar.locator("text=学生管理")).not_to_be_visible()
    context.close()
    browser.close()

def test_student_sidebar(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS)
    sidebar = page.locator(SIDEBAR_SELECTOR)
    expect(sidebar.locator("text=课程中心")).to_be_visible()
    expect(sidebar.locator("text=题库中心")).not_to_be_visible()
    expect(sidebar.locator("text=收藏题库")).to_be_visible()
    expect(sidebar.locator("text=AI自测")).to_be_visible()
    expect(sidebar.locator("text=创建课程")).not_to_be_visible()
    expect(sidebar.locator("text=���密知识库设置")).not_to_be_visible()
    context.close()
    browser.close()

def test_tutor_sidebar(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS)
    sidebar = page.locator(SIDEBAR_SELECTOR)
    expect(sidebar.locator("text=教师管理")).to_be_visible()
    expect(sidebar.locator("text=学生管理")).to_be_visible()
    expect(sidebar.locator("text=系统概览")).to_be_visible()
    expect(sidebar.locator("text=课程中心")).not_to_be_visible()
    expect(sidebar.locator("text=题库中心")).not_to_be_visible()
    context.close()
    browser.close()
